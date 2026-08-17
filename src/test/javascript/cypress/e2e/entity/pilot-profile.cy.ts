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

describe('PilotProfile e2e test', () => {
  const pilotProfilePageUrl = '/pilot-profile';
  let username: string;
  let password: string;
  const pilotProfileSample = {
    totalXp: 27172,
    rankTier: 'SENIOR_FIRST_OFFICER',
    totalFlightHours: 5576.22,
    totalNightFlightHours: 26829.2,
    totalIfrFlightHours: 26513,
    flightsCompleted: 30934,
  };

  let pilotProfile;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/pilot-profiles+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/pilot-profiles').as('postEntityRequest');
    cy.intercept('DELETE', '/api/pilot-profiles/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (pilotProfile) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/pilot-profiles/${pilotProfile.id}`,
      }).then(() => {
        pilotProfile = undefined;
      });
    }
  });

  it('PilotProfiles menu should load PilotProfiles page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('pilot-profile');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('PilotProfile').should('exist');
    cy.location('pathname').should('eq', pilotProfilePageUrl);
  });

  describe('PilotProfile page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(pilotProfilePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create PilotProfile page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${pilotProfilePageUrl}/new`);
        cy.getEntityCreateUpdateHeading('PilotProfile');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', pilotProfilePageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/pilot-profiles',
          body: pilotProfileSample,
        }).then(({ body }) => {
          pilotProfile = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/pilot-profiles+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [pilotProfile],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(pilotProfilePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details PilotProfile page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('pilotProfile');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', pilotProfilePageUrl);
      });

      it('edit button click should load edit PilotProfile page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PilotProfile');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', pilotProfilePageUrl);
      });

      it('edit button click should load edit PilotProfile page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PilotProfile');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', pilotProfilePageUrl);
      });

      it('last delete button click should delete instance of PilotProfile', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('pilotProfile').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', pilotProfilePageUrl);

        pilotProfile = undefined;
      });
    });
  });

  describe('new PilotProfile page', () => {
    beforeEach(() => {
      cy.visit(pilotProfilePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('PilotProfile');
    });

    it('should create an instance of PilotProfile', () => {
      cy.get(`[data-cy="totalXp"]`).type('18744');
      cy.get(`[data-cy="totalXp"]`).should('have.value', '18744');

      cy.get(`[data-cy="rankTier"]`).select('CAPTAIN');

      cy.get(`[data-cy="totalFlightHours"]`).type('6003.43');
      cy.get(`[data-cy="totalFlightHours"]`).should('have.value', '6003.43');

      cy.get(`[data-cy="totalNightFlightHours"]`).type('26743.9');
      cy.get(`[data-cy="totalNightFlightHours"]`).should('have.value', '26743.9');

      cy.get(`[data-cy="totalIfrFlightHours"]`).type('13520.41');
      cy.get(`[data-cy="totalIfrFlightHours"]`).should('have.value', '13520.41');

      cy.get(`[data-cy="flightsCompleted"]`).type('7708');
      cy.get(`[data-cy="flightsCompleted"]`).should('have.value', '7708');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        pilotProfile = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', pilotProfilePageUrl);
    });
  });
});
