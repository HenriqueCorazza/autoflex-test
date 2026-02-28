// cypress/e2e/02_products.cy.js

describe('Products - CRUD', () => {
    const product = {
        name: 'Cypress Widget',
        sku: 'WGT-CY-TEST',
        value: '299.99',
    }

    before(() => {
        cy.navigateTo('products')
        cy.get('table', { timeout: 15000 }).should('be.visible')
        cy.get('body').then($body => {
            if (!$body.text().includes(product.name)) {
                cy.openCreateModal()
                cy.get('[data-cy="input-product-name"]').type(product.name)
                cy.get('[data-cy="input-sku"]').type(product.sku)
                cy.get('[data-cy="input-value"]').type(product.value)

                // Primeiro clica em + Add para aparecer o select
                cy.contains('button', /^\+ Add$/i).click()

                // Agora o select existe
                cy.get('select').first().select(1)
                cy.get('input[type="number"]').last().clear().type('5')

                cy.contains('button', /save|create/i).last().click()
                cy.get('.modal').should('not.exist')
                cy.contains(product.name, { timeout: 10000 }).should('be.visible')
            }
        })
    })

    after(() => {
        cy.navigateTo('products')
        cy.get('table', { timeout: 15000 }).should('be.visible')
        cy.get('body').then($body => {
            if ($body.text().includes(product.name)) {
                cy.contains('tr', product.name).within(() => {
                    cy.contains('button', /delete/i).click()
                })
                cy.contains('Confirm Delete').should('be.visible')
                cy.contains('button', /yes.*delete/i).click()
                cy.contains(product.name, { timeout: 10000 }).should('not.exist')
            }
        })
    })

    beforeEach(() => {
        cy.navigateTo('products')
        cy.get('table', { timeout: 15000 }).should('be.visible')
    })

    it('should display the Products page', () => {
        cy.contains('Products').should('be.visible')
        cy.get('table').should('exist')
    })

    it('should open and close the create modal', () => {
        cy.openCreateModal()
        cy.closeModal()
    })

    it('should show validation when submitting empty form', () => {
        cy.openCreateModal()
        cy.contains('button', /save|create/i).last().click()
        cy.get('.modal').should('be.visible')
        cy.closeModal()
    })

    it('should create a new product with materials', () => {
        cy.get('body').then($body => {
            if ($body.text().includes(product.name)) {
                cy.contains('tr', product.name).should('be.visible')
            } else {
                cy.openCreateModal()
                cy.get('[data-cy="input-product-name"]').type(product.name)
                cy.get('[data-cy="input-sku"]').type(product.sku)
                cy.get('[data-cy="input-value"]').type(product.value)

                cy.get('select').then($selects => {
                    if ($selects.length > 0) {
                        cy.wrap($selects).first().select(1)
                        cy.get('input[type="number"]').last().clear().type('5')
                        cy.contains('button', /^\+ Add$/i).click()
                    }
                })

                cy.contains('button', /save|create/i).last().click()
                cy.get('.modal').should('not.exist')
                cy.contains(product.name, { timeout: 10000 }).should('be.visible')
            }
        })
    })

    it('should edit an existing product', () => {
        cy.contains('tr', product.name).within(() => {
            cy.contains('button', /edit/i).click()
        })

        cy.get('.modal').should('be.visible')
        cy.get('[data-cy="input-value"]').clear().type('399.99')
        cy.contains('button', /save|update/i).last().click()

        cy.get('.modal').should('not.exist')
        cy.contains('tr', product.name).contains('399').should('be.visible')
    })

    it('should cancel deletion when clicking Cancel', () => {
        cy.contains('tr', product.name).within(() => {
            cy.contains('button', /delete/i).click()
        })
        cy.contains('Confirm Delete').should('be.visible')
        cy.contains('button', /cancel/i).click()
        cy.get('.modal').should('not.exist')
        cy.contains(product.name).should('be.visible')
    })

    it('should delete a product with confirmation modal', () => {
        cy.contains('tr', product.name).within(() => {
            cy.contains('button', /delete/i).click()
        })

        cy.contains('Confirm Delete').should('be.visible')
        cy.contains('button', /yes.*delete/i).click()

        cy.contains(product.name, { timeout: 10000 }).should('not.exist')
    })
})