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

describe('Aircraft e2e test', () => {
  const aircraftPageUrl = '/aircraft';
  let username: string;
  let password: string;
  const aircraftSample = {
    tailNumber: 'battle dai',
    modelName: 'guilty',
    icaoType: 'weig',
    category: 'NARROWBODY_JET',
    cruiseSpeedKnots: 312,
    maxRangeNm: 1011,
    fuelBurnGph: 551.06,
    minRunwayLengthFt: 6388,
  };

  let aircraft;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/aircraft+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/aircraft').as('postEntityRequest');
    cy.intercept('DELETE', '/api/aircraft/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (aircraft) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/aircraft/${aircraft.id}`,
      }).then(() => {
        aircraft = undefined;
      });
    }
  });

  it('Aircrafts menu should load Aircrafts page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('aircraft');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Aircraft').should('exist');
    cy.location('pathname').should('eq', aircraftPageUrl);
  });

  describe('Aircraft page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(aircraftPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Aircraft page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${aircraftPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('Aircraft');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', aircraftPageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/aircraft',
          body: aircraftSample,
        }).then(({ body }) => {
          aircraft = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/aircraft+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [aircraft],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(aircraftPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Aircraft page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('aircraft');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', aircraftPageUrl);
      });

      it('edit button click should load edit Aircraft page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Aircraft');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', aircraftPageUrl);
      });

      it('edit button click should load edit Aircraft page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Aircraft');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', aircraftPageUrl);
      });

      it('last delete button click should delete instance of Aircraft', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('aircraft').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', aircraftPageUrl);

        aircraft = undefined;
      });
    });
  });

  describe('new Aircraft page', () => {
    beforeEach(() => {
      cy.visit(aircraftPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Aircraft');
    });

    it('should create an instance of Aircraft', () => {
      cy.get(`[data-cy="tailNumber"]`).type('task ew');
      cy.get(`[data-cy="tailNumber"]`).should('have.value', 'task ew');

      cy.get(`[data-cy="modelName"]`).type('minus');
      cy.get(`[data-cy="modelName"]`).should('have.value', 'minus');

      cy.get(`[data-cy="icaoType"]`).type('furt');
      cy.get(`[data-cy="icaoType"]`).should('have.value', 'furt');

      cy.get(`[data-cy="category"]`).select('REGIONAL_JET');

      cy.get(`[data-cy="cruiseSpeedKnots"]`).type('183');
      cy.get(`[data-cy="cruiseSpeedKnots"]`).should('have.value', '183');

      cy.get(`[data-cy="maxRangeNm"]`).type('1887');
      cy.get(`[data-cy="maxRangeNm"]`).should('have.value', '1887');

      cy.get(`[data-cy="serviceCeilingFt"]`).type('13931');
      cy.get(`[data-cy="serviceCeilingFt"]`).should('have.value', '13931');

      cy.get(`[data-cy="fuelBurnGph"]`).type('3247.76');
      cy.get(`[data-cy="fuelBurnGph"]`).should('have.value', '3247.76');

      cy.get(`[data-cy="minRunwayLengthFt"]`).type('5957');
      cy.get(`[data-cy="minRunwayLengthFt"]`).should('have.value', '5957');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        aircraft = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', aircraftPageUrl);
    });
  });
});
