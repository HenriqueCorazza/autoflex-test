// cypress/e2e/01_rawMaterials.cy.js

describe('Raw Materials - CRUD', () => {
    const material = {
        name: 'Cypress Steel',
        sku: 'STL-CY-TEST',
        stock: '25',
    }

    before(() => {
        cy.navigateTo('rawMaterials')
        cy.get('table', { timeout: 15000 }).should('be.visible')
        cy.get('body').then($body => {
            if (!$body.text().includes(material.name)) {
                cy.openCreateModal()
                cy.get('[data-cy="input-material-name"]').type(material.name)
                cy.get('[data-cy="input-sku"]').type(material.sku)
                cy.get('[data-cy="input-stock"]').type(material.stock)
                cy.contains('button', /save|create/i).last().click()
                cy.get('.modal').should('not.exist')
                cy.contains(material.name, { timeout: 10000 }).should('be.visible')
            }
        })
    })

    after(() => {
        cy.navigateTo('rawMaterials')
        cy.get('table', { timeout: 15000 }).should('be.visible')
        cy.get('body').then($body => {
            if ($body.text().includes(material.name)) {
                cy.contains('tr', material.name).within(() => {
                    cy.contains('button', /delete/i).click()
                })
                cy.contains('Confirm Delete').should('be.visible')
                cy.contains('button', /yes.*delete/i).click()
                cy.contains(material.name, { timeout: 10000 }).should('not.exist')
            }
        })
    })

    beforeEach(() => {
        cy.navigateTo('rawMaterials')
        cy.get('table', { timeout: 15000 }).should('be.visible')
    })

    it('should display the Raw Materials page', () => {
        cy.contains('Raw Materials').should('be.visible')
        cy.get('table').should('exist')
    })

    it('should open and close the create modal', () => {
        cy.openCreateModal()
        cy.closeModal()
    })

    it('should show validation errors when submitting empty form', () => {
        cy.openCreateModal()
        cy.contains('button', /save|create/i).last().click()
        cy.get('.modal').should('be.visible')
        cy.closeModal()
    })

    it('should create a new raw material', () => {
        // Se já existe (criado no before), apenas confirma que está visível
        cy.get('body').then($body => {
            if ($body.text().includes(material.name)) {
                cy.contains('tr', material.name).should('be.visible')
            } else {
                cy.openCreateModal()
                cy.get('[data-cy="input-material-name"]').type(material.name)
                cy.get('[data-cy="input-sku"]').type(material.sku)
                cy.get('[data-cy="input-stock"]').type(material.stock)
                cy.contains('button', /save|create/i).last().click()
                cy.get('.modal').should('not.exist')
                cy.contains(material.name, { timeout: 10000 }).should('be.visible')
            }
        })
    })

    it('should edit an existing raw material', () => {
        cy.contains('tr', material.name).within(() => {
            cy.contains('button', /edit/i).click()
        })

        cy.get('.modal').should('be.visible')
        cy.get('[data-cy="input-stock"]').clear().type('99')
        cy.contains('button', /save|update/i).last().click()

        cy.get('.modal').should('not.exist')
        cy.contains('tr', material.name).contains('99').should('be.visible')
    })

    it('should cancel deletion when clicking Cancel', () => {
        cy.contains('tr', material.name).within(() => {
            cy.contains('button', /delete/i).click()
        })
        cy.contains('Confirm Delete').should('be.visible')
        cy.contains('button', /cancel/i).click()
        cy.get('.modal').should('not.exist')
        cy.contains(material.name).should('be.visible')
    })

    it('should delete a raw material with confirmation modal', () => {
        cy.contains('tr', material.name).within(() => {
            cy.contains('button', /delete/i).click()
        })

        cy.contains('Confirm Delete').should('be.visible')
        cy.contains('button', /yes.*delete/i).click()

        cy.contains(material.name, { timeout: 10000 }).should('not.exist')
    })
})