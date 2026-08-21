import { Component, inject } from '@angular/core';
import { AircraftService } from '../../entities/aircraft/service/aircraft.service';
import { AirportService } from '../../entities/airport/service/airport.service';
import { FlightDispatch } from '../../core/services/flight-dispatch';
import { IAirport } from '../../entities/airport/airport.model';
import { IAircraft } from '../../entities/aircraft/aircraft.model';
import { RoutePlan } from '../../core/models/route-plan';
import { NgForOf, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'jhi-flight-dispatcher',
  imports: [NgIf, FormsModule, NgForOf],
  templateUrl: './flight-dispatcher.html',
  styleUrl: './flight-dispatcher.scss',
})
export class FlightDispatcher {
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

  ngOnInit() {
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

  onCalculateRoute() {
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

    this.flightDispatchService
      .calculateRoute(this.selectedDepartureAirportId, this.selectedArrivalAirportId, this.selectedAircraftId)
      .subscribe({
        next: plan => {
          this.routePlan = plan;
          this.isLoading = false;
        },
        error: err => {
          this.errorMessage = 'Calculation Error. Please verify backend server is running.';
          this.isLoading = false;
        },
      });
    return;
  }
}
