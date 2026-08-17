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

describe('Airport e2e test', () => {
  const airportPageUrl = '/airport';
  let username: string;
  let password: string;
  const airportSample = {
    icaoCode: 'unke',
    name: 'than once',
    city: 'New Lyle',
    country: 'Trinidad and Tobago',
    latitude: -89.13,
    longitude: -118.37,
    longestRunwayFt: 19758,
  };

  let airport;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/airports+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/airports').as('postEntityRequest');
    cy.intercept('DELETE', '/api/airports/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (airport) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/airports/${airport.id}`,
      }).then(() => {
        airport = undefined;
      });
    }
  });

  it('Airports menu should load Airports page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('airport');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Airport').should('exist');
    cy.location('pathname').should('eq', airportPageUrl);
  });

  describe('Airport page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(airportPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Airport page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${airportPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('Airport');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', airportPageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/airports',
          body: airportSample,
        }).then(({ body }) => {
          airport = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/airports+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/airports?page=0&size=20>; rel="last",<http://localhost/api/airports?page=0&size=20>; rel="first"',
              },
              body: [airport],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(airportPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Airport page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('airport');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', airportPageUrl);
      });

      it('edit button click should load edit Airport page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Airport');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', airportPageUrl);
      });

      it('edit button click should load edit Airport page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Airport');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', airportPageUrl);
      });

      it('last delete button click should delete instance of Airport', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('airport').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', airportPageUrl);

        airport = undefined;
      });
    });
  });

  describe('new Airport page', () => {
    beforeEach(() => {
      cy.visit(airportPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Airport');
    });

    it('should create an instance of Airport', () => {
      cy.get(`[data-cy="icaoCode"]`).type('the');
      cy.get(`[data-cy="icaoCode"]`).should('have.value', 'the');

      cy.get(`[data-cy="iataCode"]`).type('in ');
      cy.get(`[data-cy="iataCode"]`).should('have.value', 'in ');

      cy.get(`[data-cy="name"]`).type('gah');
      cy.get(`[data-cy="name"]`).should('have.value', 'gah');

      cy.get(`[data-cy="city"]`).type('East Lanefield');
      cy.get(`[data-cy="city"]`).should('have.value', 'East Lanefield');

      cy.get(`[data-cy="country"]`).type('Tonga');
      cy.get(`[data-cy="country"]`).should('have.value', 'Tonga');

      cy.get(`[data-cy="latitude"]`).type('-56.56');
      cy.get(`[data-cy="latitude"]`).should('have.value', '-56.56');

      cy.get(`[data-cy="longitude"]`).type('-143.89');
      cy.get(`[data-cy="longitude"]`).should('have.value', '-143.89');

      cy.get(`[data-cy="elevationFt"]`).type('3830');
      cy.get(`[data-cy="elevationFt"]`).should('have.value', '3830');

      cy.get(`[data-cy="longestRunwayFt"]`).type('4702');
      cy.get(`[data-cy="longestRunwayFt"]`).should('have.value', '4702');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        airport = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', airportPageUrl);
    });
  });
});
