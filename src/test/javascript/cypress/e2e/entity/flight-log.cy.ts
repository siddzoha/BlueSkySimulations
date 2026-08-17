import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('FlightLog e2e test', () => {
  const flightLogPageUrl = '/flight-log';
  let username: string;
  let password: string;
  const flightLogSample = {
    departureTime: '2023-12-18T02:28:14.568Z',
    arrivalTime: '2023-12-18T01:46:18.146Z',
    durationHours: 4.19,
    distanceNm: 4117,
    flightRules: 'VFR',
  };

  let flightLog;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/flight-logs+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/flight-logs').as('postEntityRequest');
    cy.intercept('DELETE', '/api/flight-logs/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (flightLog) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/flight-logs/${flightLog.id}`,
      }).then(() => {
        flightLog = undefined;
      });
    }
  });

  it('FlightLogs menu should load FlightLogs page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('flight-log');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('FlightLog').should('exist');
    cy.location('pathname').should('eq', flightLogPageUrl);
  });

  describe('FlightLog page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(flightLogPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create FlightLog page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${flightLogPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('FlightLog');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', flightLogPageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/flight-logs',
          body: flightLogSample,
        }).then(({ body }) => {
          flightLog = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/flight-logs+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/flight-logs?page=0&size=20>; rel="last",<http://localhost/api/flight-logs?page=0&size=20>; rel="first"',
              },
              body: [flightLog],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(flightLogPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details FlightLog page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('flightLog');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', flightLogPageUrl);
      });

      it('edit button click should load edit FlightLog page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('FlightLog');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', flightLogPageUrl);
      });

      it('edit button click should load edit FlightLog page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('FlightLog');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', flightLogPageUrl);
      });

      it('last delete button click should delete instance of FlightLog', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('flightLog').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', flightLogPageUrl);

        flightLog = undefined;
      });
    });
  });

  describe('new FlightLog page', () => {
    beforeEach(() => {
      cy.visit(flightLogPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('FlightLog');
    });

    it('should create an instance of FlightLog', () => {
      cy.get(`[data-cy="departureTime"]`).type('2023-12-17T21:19');
      cy.get(`[data-cy="departureTime"]`).blur();
      cy.get(`[data-cy="departureTime"]`).should('have.value', '2023-12-17T21:19');

      cy.get(`[data-cy="arrivalTime"]`).type('2023-12-18T01:40');
      cy.get(`[data-cy="arrivalTime"]`).blur();
      cy.get(`[data-cy="arrivalTime"]`).should('have.value', '2023-12-18T01:40');

      cy.get(`[data-cy="durationHours"]`).type('29.71');
      cy.get(`[data-cy="durationHours"]`).should('have.value', '29.71');

      cy.get(`[data-cy="distanceNm"]`).type('6337');
      cy.get(`[data-cy="distanceNm"]`).should('have.value', '6337');

      cy.get(`[data-cy="cruiseAltitudeFt"]`).type('49383');
      cy.get(`[data-cy="cruiseAltitudeFt"]`).should('have.value', '49383');

      cy.get(`[data-cy="fuelUsedGallons"]`).type('6764.27');
      cy.get(`[data-cy="fuelUsedGallons"]`).should('have.value', '6764.27');

      cy.get(`[data-cy="xpEarned"]`).type('3285');
      cy.get(`[data-cy="xpEarned"]`).should('have.value', '3285');

      cy.get(`[data-cy="flightRules"]`).select('IFR');

      cy.get(`[data-cy="remarks"]`).type('for gummy');
      cy.get(`[data-cy="remarks"]`).should('have.value', 'for gummy');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        flightLog = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', flightLogPageUrl);
    });
  });
});
