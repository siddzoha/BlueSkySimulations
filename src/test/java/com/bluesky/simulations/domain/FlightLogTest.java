package com.bluesky.simulations.domain;

import static com.bluesky.simulations.domain.AircraftTestSamples.*;
import static com.bluesky.simulations.domain.AirportTestSamples.*;
import static com.bluesky.simulations.domain.FlightLogTestSamples.*;
import static com.bluesky.simulations.domain.PilotProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bluesky.simulations.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FlightLogTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FlightLog.class);
        FlightLog flightLog1 = getFlightLogSample1();
        FlightLog flightLog2 = new FlightLog();
        assertThat(flightLog1).isNotEqualTo(flightLog2);

        flightLog2.setId(flightLog1.getId());
        assertThat(flightLog1).isEqualTo(flightLog2);

        flightLog2 = getFlightLogSample2();
        assertThat(flightLog1).isNotEqualTo(flightLog2);
    }

    @Test
    void aircraftTest() {
        FlightLog flightLog = getFlightLogRandomSampleGenerator();
        Aircraft aircraftBack = getAircraftRandomSampleGenerator();

        flightLog.setAircraft(aircraftBack);
        assertThat(flightLog.getAircraft()).isEqualTo(aircraftBack);

        flightLog.aircraft(null);
        assertThat(flightLog.getAircraft()).isNull();
    }

    @Test
    void departureAirportTest() {
        FlightLog flightLog = getFlightLogRandomSampleGenerator();
        Airport airportBack = getAirportRandomSampleGenerator();

        flightLog.setDepartureAirport(airportBack);
        assertThat(flightLog.getDepartureAirport()).isEqualTo(airportBack);

        flightLog.departureAirport(null);
        assertThat(flightLog.getDepartureAirport()).isNull();
    }

    @Test
    void arrivalAirportTest() {
        FlightLog flightLog = getFlightLogRandomSampleGenerator();
        Airport airportBack = getAirportRandomSampleGenerator();

        flightLog.setArrivalAirport(airportBack);
        assertThat(flightLog.getArrivalAirport()).isEqualTo(airportBack);

        flightLog.arrivalAirport(null);
        assertThat(flightLog.getArrivalAirport()).isNull();
    }

    @Test
    void pilotTest() {
        FlightLog flightLog = getFlightLogRandomSampleGenerator();
        PilotProfile pilotProfileBack = getPilotProfileRandomSampleGenerator();

        flightLog.setPilot(pilotProfileBack);
        assertThat(flightLog.getPilot()).isEqualTo(pilotProfileBack);

        flightLog.pilot(null);
        assertThat(flightLog.getPilot()).isNull();
    }
}
