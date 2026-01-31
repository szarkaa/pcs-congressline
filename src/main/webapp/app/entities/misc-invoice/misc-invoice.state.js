(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('misc-invoice', {
            parent: 'administration',
            url: '/misc-invoice',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN'],
                pageTitle: 'pcsApp.miscInvoice.home.title'
            },
            views: {
                'content@base': {
                    templateUrl: 'app/entities/misc-invoice/misc-invoices.html',
                    controller: 'MiscInvoiceController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                miscInvoice: ['CongressSelector', function (CongressSelector) {
                    return {
                        id: null,
                        name1: null,
                        name2: null,
                        name3: null,
                        vatRegNumber: null,
                        street: null,
                        city: null,
                        zipCode: null,
                        country: CongressSelector.getSelectedCongress().defaultCountry ? CongressSelector.getSelectedCongress().defaultCountry.code : null,
                        optionalText: null,
                        startDate: new Date(CongressSelector.getSelectedCongress().startDate),
                        endDate: new Date(CongressSelector.getSelectedCongress().endDate),
                        dateOfFulfilment: new Date(),
                        paymentDeadline: (new Date()).setDate((new Date()).getDate() + 10),
                        billingMethod: 'TRANSFER',
                        language: 'hu',
                        invoiceType: null,
                        navVatCategory: null,
                        customInvoiceEmail: null,
                        bankAccountId: null,
                        createdDate: new Date(),
                        miscInvoiceItems: [],
                        congressId: CongressSelector.getSelectedCongress().id
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('miscInvoice');
                    $translatePartialLoader.addPart('navVatCategory');
                    $translatePartialLoader.addPart('invoiceType');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('misc-invoice.setup', {
            parent: 'misc-invoice',
            url: '/setup',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
            },
            views: {
                'content@base': {
                    templateUrl: 'app/entities/misc-invoice/misc-invoice-setup-dialog.html',
                    controller: 'MiscInvoiceSetupDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('invoice');
                    $translatePartialLoader.addPart('optionalText');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('misc-invoice.optional-text-new', {
            parent: 'misc-invoice',
            url: '/optional-text/new',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'CongressSelector', function($stateParams, $state, $uibModal, CongressSelector) {
                $uibModal.open({
                    templateUrl: 'app/entities/optional-text/optional-text-dialog.html',
                    controller: 'OptionalTextDialogController',
                    controllerAs: 'vm',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                id: null,
                                name: null,
                                optionalText: null,
                                congressId: CongressSelector.getSelectedCongress().id
                            };
                        }
                    }
                }).result.then(function(result) {
                        $state.go('misc-invoice.setup', {reload: true});
                    }, function() {
                        $state.go('misc-invoice.setup');
                    })
            }]
        })
        .state('misc-invoice.storno', {
            parent: 'misc-invoice',
            url: '/{invoiceCongressId}/storno',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/misc-invoice/misc-invoice-storno-dialog.html',
                    controller: 'MiscInvoiceStornoController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['MiscInvoice', function(MiscInvoice) {
                            return MiscInvoice.get({id : $stateParams.invoiceCongressId});
                        }]
                    }
                }).result.then(function(result) {
                        $state.go('misc-invoice', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
            }]
        })
        .state('misc-invoice.set-payment-date', {
            parent: 'misc-invoice',
            url: '/{id}/set-payment-date',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/misc-invoice/misc-invoice-set-payment-date-dialog.html',
                    controller: 'MiscInvoiceSetPaymentDateDialogController',
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
                    $state.go('misc-invoice', null, {reload: 'misc-invoice'});
                }, function () {
                    $state.go('misc-invoice');
                });
            }]
        });
    }

})();
