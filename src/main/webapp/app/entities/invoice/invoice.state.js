(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('invoice', {
            parent: 'registration',
            url: '/invoice',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN'],
                pageTitle: 'pcsApp.invoice.home.title'
            },
            views: {
                'content@base': {
                    templateUrl: 'app/entities/invoice/invoices.html',
                    controller: 'InvoiceController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                invoice: ['CongressSelector', 'DateUtils', function (CongressSelector, DateUtils) {
                    return {
                        name1: '',
                        name2: '',
                        invoiceName: '',
                        lastName: '',
                        firstName: '',
                        vatRegNumber: '',
                        city: '',
                        zipCode: '',
                        street: '',
                        country: CongressSelector.getSelectedCongress().defaultCountry ? CongressSelector.getSelectedCongress().defaultCountry.code : null,
                        optionalText: null,
                        startDate: new Date(CongressSelector.getSelectedCongress().startDate),
                        endDate: new Date(CongressSelector.getSelectedCongress().endDate),
                        dateOfFulfilment: new Date(),
                        paymentDeadline: (new Date()).setDate((new Date()).getDate() + 10),
                        billingMethod: 'TRANSFER',
                        language: 'hu',
                        navVatCategory: null,
                        createdDate: new Date(),
                        bankAccount: null,
                        registration: null,
                        ignoredChargeableItems: null,
                        ignoredChargedServices: null
                    };
                }],
                invoices: ['$stateParams', 'Invoice', function ($stateParams, Invoice) {
                    return Invoice.queryByRegistrationId({id: $stateParams.registrationId});
                }],
                invoicedChargeableItemIds: ['$stateParams', 'Invoice', function ($stateParams, Invoice) {
                    return Invoice.getInvoicedChargeableItemIdsByRegistrationId({id: $stateParams.registrationId});
                }],
                invoicedChargedServiceIds: ['$stateParams', 'Invoice', function ($stateParams, Invoice) {
                    return Invoice.getInvoicedChargedServiceIdsByRegistrationId({id: $stateParams.registrationId});
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('invoice');
                    $translatePartialLoader.addPart('optionalText');
                    $translatePartialLoader.addPart('navVatCategory');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('invoice.new', {
            parent: 'invoice',
            url: '/new',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
            },
            views: {
                'content@base': {
                    templateUrl: 'app/entities/invoice/invoice-dialog.html',
                    controller: 'InvoiceDialogController',
                    controllerAs: 'vm'
                }
            }
        })
        .state('invoice.setup', {
            parent: 'invoice',
            url: '/setup',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
            },
            views: {
                'content@base': {
                    templateUrl: 'app/entities/invoice/invoice-setup-dialog.html',
                    controller: 'InvoiceSetupDialogController',
                    controllerAs: 'vm'
                }
            }
        })
        .state('invoice.optional-text-new', {
            parent: 'invoice',
            url: '/optional-text/new',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'CongressSelector', function($stateParams, $state, $uibModal, CongressSelector) {
                $uibModal.open({
                    templateUrl: 'app/entities/optional-text/optional-text-dialog.html',
                    controller: 'OptionalTextDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                optionalText: null,
                                id: null,
                                congress: CongressSelector.getSelectedCongress()
                            };
                        }
                    }
                }).result.then(function(result) {
                        $state.go('invoice.setup', {reload: true});
                    }, function() {
                        $state.go('invoice.setup');
                    })
            }]
        })
        .state('invoice.set-payment-date', {
            parent: 'invoice',
            url: '/{id}/set-payment-date',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/invoice/invoice-set-payment-date-dialog.html',
                    controller: 'InvoiceSetPaymentDateDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                paymentDate: new Date(),
                                id: $stateParams.id
                            };
                        }
                    }
                }).result.then(function () {
                    $state.go('invoice', null, {reload: 'invoice'});
                }, function () {
                    $state.go('invoice');
                });
            }]
        })
        .state('invoice.storno', {
            parent: 'invoice',
            url: '/{invoiceId}/storno',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/invoice/invoice-storno-dialog.html',
                    controller: 'InvoiceStornoController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'md',
                    resolve: {
                        entity: ['Invoice', function(Invoice) {
                            return Invoice.get({id : $stateParams.invoiceId}).$promise;
                        }]
                    }
                }).result.then(function(result) {
                        $state.go('invoice', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
            }]
        });
    }

})();
