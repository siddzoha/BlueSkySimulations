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

describe('Achievement e2e test', () => {
  const achievementPageUrl = '/achievement';
  let username: string;
  let password: string;
  const achievementSample = { code: 'ew', title: 'colossal round woot', description: 'aw fabricate', xpReward: 11640 };

  let achievement;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/achievements+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/achievements').as('postEntityRequest');
    cy.intercept('DELETE', '/api/achievements/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (achievement) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/achievements/${achievement.id}`,
      }).then(() => {
        achievement = undefined;
      });
    }
  });

  it('Achievements menu should load Achievements page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('achievement');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Achievement').should('exist');
    cy.location('pathname').should('eq', achievementPageUrl);
  });

  describe('Achievement page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(achievementPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Achievement page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${achievementPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('Achievement');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', achievementPageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/achievements',
          body: achievementSample,
        }).then(({ body }) => {
          achievement = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/achievements+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [achievement],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(achievementPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Achievement page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('achievement');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', achievementPageUrl);
      });

      it('edit button click should load edit Achievement page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Achievement');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', achievementPageUrl);
      });

      it('edit button click should load edit Achievement page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Achievement');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', achievementPageUrl);
      });

      it('last delete button click should delete instance of Achievement', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('achievement').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', achievementPageUrl);

        achievement = undefined;
      });
    });
  });

  describe('new Achievement page', () => {
    beforeEach(() => {
      cy.visit(achievementPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Achievement');
    });

    it('should create an instance of Achievement', () => {
      cy.get(`[data-cy="code"]`).type('eventually impanel');
      cy.get(`[data-cy="code"]`).should('have.value', 'eventually impanel');

      cy.get(`[data-cy="title"]`).type('stable considering');
      cy.get(`[data-cy="title"]`).should('have.value', 'stable considering');

      cy.get(`[data-cy="description"]`).type('bonnet');
      cy.get(`[data-cy="description"]`).should('have.value', 'bonnet');

      cy.get(`[data-cy="badgeIcon"]`).type('welcome where diligently');
      cy.get(`[data-cy="badgeIcon"]`).should('have.value', 'welcome where diligently');

      cy.get(`[data-cy="xpReward"]`).type('24338');
      cy.get(`[data-cy="xpReward"]`).should('have.value', '24338');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        achievement = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', achievementPageUrl);
    });
  });
});
