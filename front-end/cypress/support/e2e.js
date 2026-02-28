// cypress/support/e2e.js
// Global configuration and custom commands

Cypress.Commands.add('navigateTo', (page) => {
    const routes = {
        products: '/',
        rawMaterials: '/raw-materials',
        suggestions: '/suggestions',
    }
    cy.visit(routes[page])
})

Cypress.Commands.add('openCreateModal', () => {
    cy.contains('button', /new|add/i, { timeout: 10000 }).first().should('be.visible').click()
    cy.get('.modal', { timeout: 10000 }).should('be.visible')
})

Cypress.Commands.add('closeModal', () => {
    cy.contains('button', /cancel/i).click()
    cy.get('.modal').should('not.exist')
})