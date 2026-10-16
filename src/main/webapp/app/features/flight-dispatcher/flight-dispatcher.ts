import { Component, inject, OnInit, AfterViewInit, NgZone, OnDestroy } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import * as L from 'leaflet';
import chroma from 'chroma-js';

import { AircraftService } from '../../entities/aircraft/service/aircraft.service';
import { AirportService } from '../../entities/airport/service/airport.service';
import { FlightDispatch } from '../../core/services/flight-dispatch';
import { IAirport } from '../../entities/airport/airport.model';
import { IAircraft } from '../../entities/aircraft/aircraft.model';
import { AircraftWeightLimits, RoutePlan } from '../../core/models/route-plan';

@Component({
  selector: 'jhi-flight-dispatcher',
  standalone: true,
  imports: [FormsModule, DecimalPipe],
  templateUrl: './flight-dispatcher.html',
  styleUrl: './flight-dispatcher.scss',
})
export class FlightDispatcher implements OnInit, AfterViewInit, OnDestroy {
  airports: IAirport[] = [];
  aircraftList: IAircraft[] = [];

  selectedAircraftId: number | null = null;
  selectedDepartureAirportId: number | null = null;
  selectedArrivalAirportId: number | null = null;

  takeoffWeightLbs: number | null = null;
  costIndex = 30;
  weightLimits: AircraftWeightLimits | null = null;

  routePlan: RoutePlan | null = null;
  isLoading = false;
  errorMessage: string | null = null;
  isFilingFlight = false;
  fileSuccessMessage: string | null = null;

  private aircraftService = inject(AircraftService);
  private airportService = inject(AirportService);
  private flightDispatchService = inject(FlightDispatch);
  private ngZone = inject(NgZone);

  private map: L.Map | null = null;
  private airportMarkersLayer: L.LayerGroup | null = null;
  private routeLayerGroup: L.LayerGroup | null = null;
  private airportMarkerMap = new Map<number, L.Marker>();
  private readonly altitudeColorScale = chroma.scale(['#1565c0', '#00e5ff', '#00e676', '#ffea00', '#ff1744']).mode('lab');

  ngOnInit(): void {
    this.airportService.query({ size: 200 }).subscribe({
      next: res => {
        this.airports = res.body ?? [];
        if (this.map) {
          this.renderAllAirportMarkers();
        }
      },
      error: () => {
        this.errorMessage = 'Failed to load airports from backend.';
      },
    });

    this.aircraftService.query({ size: 100 }).subscribe({
      next: res => {
        this.aircraftList = res.body ?? [];
      },
      error: () => {
        this.errorMessage = 'Failed to load aircraft list.';
      },
    });
  }

  ngAfterViewInit(): void {
    this.initMap();
  }

  ngOnDestroy(): void {
    if (this.map) {
      this.map.remove();
      this.map = null;
    }
  }

  selectAirportFromMap(airportId: number, type: 'DEP' | 'ARR'): void {
    if (type === 'DEP') {
      if (this.selectedArrivalAirportId === airportId) {
        this.selectedArrivalAirportId = this.selectedDepartureAirportId;
      }
      this.selectedDepartureAirportId = airportId;
    } else {
      if (this.selectedDepartureAirportId === airportId) {
        this.selectedDepartureAirportId = this.selectedArrivalAirportId;
      }
      this.selectedArrivalAirportId = airportId;
    }

    this.refreshMarkerIcons();
    this.checkAutoCalculate();
  }

  onDepartureChange(): void {
    if (this.selectedDepartureAirportId && this.selectedDepartureAirportId === this.selectedArrivalAirportId) {
      this.selectedArrivalAirportId = null;
    }
    const airport = this.airports.find(a => a.id === this.selectedDepartureAirportId);
    if (airport?.latitude != null && airport.longitude != null && this.map) {
      this.map.panTo([airport.latitude, airport.longitude], { animate: true });
    }
    this.refreshMarkerIcons();
    this.checkAutoCalculate();
  }

  onArrivalChange(): void {
    if (this.selectedArrivalAirportId && this.selectedArrivalAirportId === this.selectedDepartureAirportId) {
      this.selectedDepartureAirportId = null;
    }
    const airport = this.airports.find(a => a.id === this.selectedArrivalAirportId);
    if (airport?.latitude != null && airport.longitude != null && this.map) {
      this.map.panTo([airport.latitude, airport.longitude], { animate: true });
    }
    this.refreshMarkerIcons();
    this.checkAutoCalculate();
  }

  onAircraftChange(): void {
    if (!this.selectedAircraftId) return;

    this.flightDispatchService.getAircraftWeightLimits(this.selectedAircraftId).subscribe({
      next: limits => {
        this.weightLimits = limits;
        this.takeoffWeightLbs = limits.recommendedTakeoffWeightLbs;
        this.checkAutoCalculate();
      },
      error: () => {
        this.weightLimits = null;
      },
    });
  }

  onParameterChange(): void {
    this.checkAutoCalculate();
  }

  onCalculateRoute(): void {
    if (!this.selectedDepartureAirportId || !this.selectedArrivalAirportId) {
      this.errorMessage = 'Please select both departure and arrival airports.';
      return;
    }

    if (this.selectedDepartureAirportId === this.selectedArrivalAirportId) {
      this.errorMessage = 'Departure and arrival airports cannot be the same.';
      return;
    }

    if (!this.selectedAircraftId) {
      this.errorMessage = 'Please select an aircraft from the dropdown above.';
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;
    this.fileSuccessMessage = null;

    this.flightDispatchService
      .calculateRoute(
        this.selectedDepartureAirportId,
        this.selectedArrivalAirportId,
        this.selectedAircraftId,
        this.takeoffWeightLbs ?? undefined,
        this.costIndex,
      )
      .subscribe({
        next: plan => {
          this.routePlan = plan;
          this.updateMapRouteWithGradient(plan);
          this.isLoading = false;
        },
        error: () => {
          this.errorMessage = 'Calculation error. Please ensure backend server is running.';
          this.isLoading = false;
        },
      });
  }

  onFileFlight(): void {
    if (!this.routePlan) return;

    this.isFilingFlight = true;
    this.fileSuccessMessage = null;
    this.errorMessage = null;

    this.flightDispatchService.fileFlight(this.routePlan).subscribe({
      next: log => {
        this.isFilingFlight = false;
        this.fileSuccessMessage = `🎉 Flight logged! Flight #${log.id} recorded (+${this.routePlan?.xpReward} XP earned).`;
      },
      error: () => {
        this.isFilingFlight = false;
        this.errorMessage = 'Failed to file flight. Make sure you are logged in.';
      },
    });
  }

  private initMap(): void {
    if (this.map) return;

    this.map = L.map('flight-map', {
      zoomControl: true,
      minZoom: 2,
    }).setView([45.0, -75.0], 5);

    // 1. ESRI Dark Gray Canvas (Default) - 100% Free, Zero Key, Zero Watermark
    const darkCanvas = L.tileLayer(
      'https://server.arcgisonline.com/ArcGIS/rest/services/Canvas/World_Dark_Gray_Base/MapServer/tile/{z}/{y}/{x}',
      {
        attribution: 'Tiles &copy; Esri &mdash; Esri, DeLorme, NAVTEQ',
        maxZoom: 16,
      },
    );

    // 2. ESRI World Imagery (Satellite) - 100% Free, Zero Key, Zero Watermark
    const satellite = L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {
      attribution: 'Tiles &copy; Esri &mdash; Source: Esri, i-cubed, USDA, USGS, AEX, GeoEye, Getmapping, Aerogrid, IGN, IGP, UPR-EGP',
      maxZoom: 19,
    });

    // 3. OpenStreetMap - 100% Free Open Data
    const osm = L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
      maxZoom: 19,
    });

    darkCanvas.addTo(this.map);

    const baseMaps = {
      '🌙 Dark Radar': darkCanvas,
      '🛰️ Satellite View': satellite,
      '🗺️ OpenStreetMap': osm,
    };
    L.control.layers(baseMaps, undefined, { position: 'topright' }).addTo(this.map);

    this.airportMarkersLayer = L.layerGroup().addTo(this.map);
    this.routeLayerGroup = L.layerGroup().addTo(this.map);

    if (this.airports.length > 0) {
      this.renderAllAirportMarkers();
    }
  }

  private createAirportIcon(airport: IAirport, isDep = false, isArr = false): L.DivIcon {
    let pinClass = 'aviation-pin';
    let fillColor = '#90caf9';

    if (isDep) {
      pinClass += ' aviation-pin-dep';
      fillColor = '#00e5ff';
    } else if (isArr) {
      pinClass += ' aviation-pin-arr';
      fillColor = '#ff1744';
    }

    const html = `
      <div class="${pinClass}">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="${fillColor}">
          <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"/>
        </svg>
        <span class="pin-icao-badge">${airport.icaoCode ?? 'APT'}</span>
      </div>
    `;

    return L.divIcon({
      className: 'custom-aviation-icon-wrapper',
      html,
      iconSize: [28, 36],
      iconAnchor: [14, 34],
      popupAnchor: [0, -32],
    });
  }

  private renderAllAirportMarkers(): void {
    if (!this.map || !this.airportMarkersLayer) return;

    this.airportMarkersLayer.clearLayers();
    this.airportMarkerMap.clear();

    const boundsLatLngs: [number, number][] = [];

    this.airports.forEach(airport => {
      if (airport.latitude == null || airport.longitude == null) return;

      const isDep = airport.id === this.selectedDepartureAirportId;
      const isArr = airport.id === this.selectedArrivalAirportId;
      const icon = this.createAirportIcon(airport, isDep, isArr);

      const marker = L.marker([airport.latitude, airport.longitude], {
        icon,
        title: `${airport.name} (${airport.icaoCode})`,
      });

      const popupContent = `
        <div class="airport-popup">
          <h6 class="mb-1">${airport.name} (${airport.icaoCode})</h6>
          <div class="airport-meta">
            ${airport.city}, ${airport.country}<br/>
            Elevation: ${airport.elevationFt ?? 0} ft • Runway: ${airport.longestRunwayFt ?? 'N/A'} ft
          </div>
          <div class="popup-actions">
            <button class="btn btn-sm btn-info text-dark fw-bold" id="btn-dep-${airport.id}">Set as DEP</button>
            <button class="btn btn-sm btn-danger fw-bold" id="btn-arr-${airport.id}">Set as ARR</button>
          </div>
        </div>
      `;

      marker.bindPopup(popupContent);

      marker.on('popupopen', () => {
        const depBtn = document.getElementById(`btn-dep-${airport.id}`);
        const arrBtn = document.getElementById(`btn-arr-${airport.id}`);

        if (depBtn) {
          depBtn.onclick = () => {
            this.ngZone.run(() => {
              this.selectAirportFromMap(airport.id, 'DEP');
              marker.closePopup();
            });
          };
        }

        if (arrBtn) {
          arrBtn.onclick = () => {
            this.ngZone.run(() => {
              this.selectAirportFromMap(airport.id, 'ARR');
              marker.closePopup();
            });
          };
        }
      });

      this.airportMarkersLayer?.addLayer(marker);
      this.airportMarkerMap.set(airport.id, marker);
      boundsLatLngs.push([airport.latitude, airport.longitude]);
    });

    if (boundsLatLngs.length > 0 && !this.routePlan) {
      this.map.fitBounds(L.latLngBounds(boundsLatLngs), { padding: [40, 40], maxZoom: 6 });
    }
  }

  private refreshMarkerIcons(): void {
    this.airportMarkerMap.forEach((marker, airportId) => {
      const airport = this.airports.find(a => a.id === airportId);
      if (!airport) return;

      const isDep = airportId === this.selectedDepartureAirportId;
      const isArr = airportId === this.selectedArrivalAirportId;
      marker.setIcon(this.createAirportIcon(airport, isDep, isArr));

      if (isDep || isArr) {
        marker.setZIndexOffset(1000);
      } else {
        marker.setZIndexOffset(0);
      }
    });
  }

  private checkAutoCalculate(): void {
    if (this.selectedDepartureAirportId && this.selectedArrivalAirportId && this.selectedAircraftId) {
      if (this.selectedDepartureAirportId !== this.selectedArrivalAirportId) {
        this.onCalculateRoute();
      }
    }
  }

  private updateMapRouteWithGradient(plan: RoutePlan): void {
    if (!this.map || !this.routeLayerGroup) return;

    this.routeLayerGroup.clearLayers();

    const waypoints = plan.profileWaypoints ?? [];
    const cruiseAlt = plan.targetCruiseAltitudeFt ?? 30000;

    const depCoord: [number, number] = [plan.departureAirport.latitude!, plan.departureAirport.longitude!];
    const arrCoord: [number, number] = [plan.arrivalAirport.latitude!, plan.arrivalAirport.longitude!];

    // Pulsing departure circle halo (Cyan)
    const depHalo = L.circleMarker(depCoord, {
      radius: 12,
      color: '#00e5ff',
      weight: 2,
      fillColor: '#00e5ff',
      fillOpacity: 0.35,
    }).bindPopup(
      `<b>DEP:</b> ${plan.departureAirport.name} (${plan.departureAirport.icaoCode})<br/>Elev: ${plan.departureAirport.elevationFt} ft`,
    );
    this.routeLayerGroup.addLayer(depHalo);

    // Pulsing arrival circle halo (Red)
    const arrHalo = L.circleMarker(arrCoord, {
      radius: 12,
      color: '#ff1744',
      weight: 2,
      fillColor: '#ff1744',
      fillOpacity: 0.35,
    }).bindPopup(
      `<b>ARR:</b> ${plan.arrivalAirport.name} (${plan.arrivalAirport.icaoCode})<br/>Elev: ${plan.arrivalAirport.elevationFt} ft`,
    );
    this.routeLayerGroup.addLayer(arrHalo);

    // Smooth Altitude Gradient Polyline
    if (waypoints.length > 1) {
      for (let i = 0; i < waypoints.length - 1; i++) {
        const p1 = waypoints[i];
        const p2 = waypoints[i + 1];

        const avgAlt = (p1.altitudeFt + p2.altitudeFt) / 2;
        const color = this.getAltitudeColor(avgAlt, cruiseAlt);

        const segment = L.polyline(
          [
            [p1.latitude, p1.longitude],
            [p2.latitude, p2.longitude],
          ],
          {
            color,
            weight: 5,
            opacity: 0.95,
          },
        );

        segment.bindTooltip(
          `
          <div class="altitude-segment-tooltip">
            <strong>${p1.flightPhase}</strong><br/>
            Altitude: ${p1.altitudeFt.toLocaleString()} ft (FL${Math.round(p1.altitudeFt / 100)})<br/>
            Distance: ${p1.distanceFromOriginNm} NM<br/>
            V/S: ${p1.verticalSpeedFpm >= 0 ? '+' : ''}${p1.verticalSpeedFpm} fpm
          </div>
          `,
          { sticky: true },
        );

        this.routeLayerGroup.addLayer(segment);
      }
    } else {
      const fallbackLine = L.polyline([depCoord, arrCoord], {
        color: '#00e5ff',
        weight: 4,
        dashArray: '6, 8',
      });
      this.routeLayerGroup.addLayer(fallbackLine);
    }

    // Midpoint Badge
    const midIndex = Math.floor(waypoints.length / 2);
    const midPoint: [number, number] =
      waypoints.length > 0
        ? [waypoints[midIndex].latitude, waypoints[midIndex].longitude]
        : [(depCoord[0] + arrCoord[0]) / 2, (depCoord[1] + arrCoord[1]) / 2];

    const midHtml = `
      <div style="background: rgba(18, 24, 38, 0.9); border: 1px solid #00e5ff; border-radius: 4px; padding: 2px 6px; color: #00e5ff; font-size: 11px; font-weight: bold; white-space: nowrap; box-shadow: 0 2px 6px rgba(0,0,0,0.7);">
        ${plan.distanceNm.toFixed(0)} NM • ${plan.initHeading}° • FL${Math.round(cruiseAlt / 100)}
      </div>
    `;

    const midMarker = L.marker(midPoint, {
      icon: L.divIcon({
        className: 'route-midpoint-badge',
        html: midHtml,
        iconSize: [110, 22],
        iconAnchor: [55, 11],
      }),
    });
    this.routeLayerGroup.addLayer(midMarker);

    this.map.fitBounds(L.latLngBounds([depCoord, arrCoord]), { padding: [60, 60] });
  }

  private getAltitudeColor(alt: number, maxAlt: number): string {
    if (maxAlt <= 0) return '#00e5ff';
    const t = Math.max(0, Math.min(1, alt / maxAlt));
    return this.altitudeColorScale(t).hex();
  }
}
