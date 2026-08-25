import { Component, inject, OnInit, AfterViewInit } from '@angular/core';
import { NgForOf, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import * as L from 'leaflet';

import { AircraftService } from '../../entities/aircraft/service/aircraft.service';
import { AirportService } from '../../entities/airport/service/airport.service';
import { FlightDispatch } from '../../core/services/flight-dispatch';
import { IAirport } from '../../entities/airport/airport.model';
import { IAircraft } from '../../entities/aircraft/aircraft.model';
import { RoutePlan } from '../../core/models/route-plan';

@Component({
  selector: 'jhi-flight-dispatcher',
  imports: [NgIf, FormsModule, NgForOf],
  templateUrl: './flight-dispatcher.html',
  styleUrl: './flight-dispatcher.scss',
})
export class FlightDispatcher implements OnInit, AfterViewInit {
  private aircraftService = inject(AircraftService);
  private airportService = inject(AirportService);
  private flightDispatchService = inject(FlightDispatch);

  airports: IAirport[] = [];
  aircraftList: IAircraft[] = [];
  selectedAircraftId: number | null = null;
  selectedDepartureAirportId: number | null = null;
  selectedArrivalAirportId: number | null = null;
  routePlan: RoutePlan | null = null;
  isLoading = false;
  errorMessage: string | null = null;
  isFilingFlight = false;
  fileSuccessMessage: string | null = null;

  private map: L.Map | null = null;
  private routeLayerGroup: L.LayerGroup | null = null;

  ngOnInit(): void {
    this.airportService.query({ size: 100 }).subscribe({
      next: res => {
        this.airports = res.body ?? [];
      },
    });

    this.aircraftService.query({ size: 100 }).subscribe({
      next: res => {
        this.aircraftList = res.body ?? [];
      },
    });
  }

  ngAfterViewInit(): void {
    this.initMap();
  }

  private initMap(): void {
    this.map = L.map('flight-map').setView([45.0, -75.0], 5);

    L.tileLayer('https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png', {
      attribution: '&copy; OpenStreetMap contributors &copy; CARTO',
      maxZoom: 19,
    }).addTo(this.map);

    this.routeLayerGroup = L.layerGroup().addTo(this.map);
  }

  private updateMapRoute(plan: RoutePlan): void {
    this.routeLayerGroup?.clearLayers();

    const dep: [number, number] = [plan.departureAirport.latitude!, plan.departureAirport.longitude!];
    const arr: [number, number] = [plan.arrivalAirport.latitude!, plan.arrivalAirport.longitude!];

    // Add Departure Marker (Cyan)
    const depMarker = L.circleMarker(dep, { color: '#00e5ff', radius: 8, fillOpacity: 0.8 }).bindPopup(
      `<b>Departure:</b> ${plan.departureAirport.name} (${plan.departureAirport.icaoCode})`,
    );
    this.routeLayerGroup?.addLayer(depMarker);

    // Add Arrival Marker (Red)
    const arrMarker = L.circleMarker(arr, { color: '#ff1744', radius: 8, fillOpacity: 0.8 }).bindPopup(
      `<b>Arrival:</b> ${plan.arrivalAirport.name} (${plan.arrivalAirport.icaoCode})`,
    );
    this.routeLayerGroup?.addLayer(arrMarker);

    // Draw Flight Path Line
    const flightPath = L.polyline([dep, arr], { color: '#00e5ff', weight: 3, dashArray: '6, 8' });
    this.routeLayerGroup?.addLayer(flightPath);

    // Auto-zoom camera to fit both airports
    this.map?.fitBounds(L.latLngBounds([dep, arr]), { padding: [50, 50] });
  }

  onCalculateRoute(): void {
    if (!this.selectedDepartureAirportId || !this.selectedArrivalAirportId || !this.selectedAircraftId) {
      this.errorMessage = 'Please select an aircraft, departure and arrival airport.';
      return;
    }

    if (this.selectedDepartureAirportId === this.selectedArrivalAirportId) {
      this.errorMessage = 'Departure and arrival airports cannot be the same.';
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;
    this.fileSuccessMessage = null;

    this.flightDispatchService
      .calculateRoute(this.selectedDepartureAirportId, this.selectedArrivalAirportId, this.selectedAircraftId)
      .subscribe({
        next: plan => {
          this.routePlan = plan;
          this.updateMapRoute(plan);
          this.isLoading = false;
        },
        error: err => {
          this.errorMessage = 'Calculation Error. Please verify backend server is running.';
          this.isLoading = false;
        },
      });
  }

  onFileFlight(): void {
    if (!this.routePlan) {
      return;
    }

    this.isFilingFlight = true;
    this.fileSuccessMessage = null;
    this.errorMessage = null;

    this.flightDispatchService.fileFlight(this.routePlan).subscribe({
      next: log => {
        this.isFilingFlight = false;
        this.fileSuccessMessage = `🎉 Flight logged! Flight #${log.id} recorded (+${this.routePlan?.xpReward} XP earned).`;
      },
      error: err => {
        this.isFilingFlight = false;
        this.errorMessage = 'Failed to file flight. Make sure you are logged in.';
      },
    });
  }
}
