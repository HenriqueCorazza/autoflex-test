// cypress/e2e/03_suggestions.cy.js

describe('Production Suggestions', () => {
    beforeEach(() => {
        cy.navigateTo('suggestions')
        cy.url().should('include', '/suggestions')
    })

    it('should display the Suggestions page', () => {
        cy.contains('Suggestion').should('be.visible')
    })

    it('should have a calculate button', () => {
        cy.contains('button', /calculate|suggest/i).should('be.visible')
    })

    it('should calculate and display production suggestions', () => {
        cy.contains('button', /calculate|suggest/i).click()

        // Aguarda o loading sumir antes de verificar o resultado
        cy.get('.loading', { timeout: 20000 }).should('not.exist')

        cy.get('body').then($body => {
            if ($body.text().match(/no products can be produced/i)) {
                cy.contains(/no products can be produced/i).should('be.visible')
            } else {
                cy.get('.suggestion-card, .suggestion-summary').should('exist')
            }
        })
    })

    it('should display total value after calculation', () => {
        cy.contains('button', /calculate|suggest/i).click()

        cy.get('.loading', { timeout: 20000 }).should('not.exist')

        cy.get('body').then($body => {
            if (!$body.text().match(/no products can be produced/i)) {
                cy.contains(/total/i).should('be.visible')
            }
        })
    })
})