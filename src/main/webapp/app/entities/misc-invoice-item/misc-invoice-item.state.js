(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('misc-invoice-item', {
            parent: 'misc-invoice',
            url: '/misc-invoice-item',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN'],
                pageTitle: 'pcsApp.miscInvoiceItem.home.title'
            },
            views: {
                'content@base': {
                    templateUrl: 'app/entities/misc-invoice-item/misc-invoice-items.html',
                    controller: 'MiscInvoiceItemController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                selectedCurrency: function () {
                    return {
                        currency: null,
                        hasValidRate: true
                    };
                },
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('miscInvoiceItem');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('misc-invoice-item.new', {
            parent: 'misc-invoice-item',
            url: '/new',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'miscInvoice', 'selectedCurrency', function($stateParams, $state, $uibModal, miscInvoice, selectedCurrency) {
                $uibModal.open({
                    templateUrl: 'app/entities/misc-invoice-item/misc-invoice-item-dialog.html',
                    controller: 'MiscInvoiceItemDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                miscService: null,
                                itemQuantity: null,
                                id: null
                            };
                        },
                        miscInvoice: function () {
                            return miscInvoice;
                        },
                        selectedCurrency: function () {
                            return selectedCurrency;
                        }
                    }
                }).result.then(function() {
                    $state.go('misc-invoice-item');
                }, function() {
                    $state.go('misc-invoice-item');
                });
            }]
        })
        .state('misc-invoice-item.edit', {
            parent: 'misc-invoice-item',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'miscInvoice', 'selectedCurrency', function($stateParams, $state, $uibModal, miscInvoice, selectedCurrency) {
                $uibModal.open({
                    templateUrl: 'app/entities/misc-invoice-item/misc-invoice-item-dialog.html',
                    controller: 'MiscInvoiceItemDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function() {
                            for (var i = 0; i < miscInvoice.miscInvoiceItems.length; i++) {
                                if (miscInvoice.miscInvoiceItems[i].id == $stateParams.id) {
                                    return miscInvoice.miscInvoiceItems[i];
                                }
                            }
                            return null;
                        },
                        miscInvoice: function () {
                            return miscInvoice;
                        },
                        selectedCurrency: function () {
                            return selectedCurrency;
                        }
                    }
                }).result.then(function() {
                    $state.go('misc-invoice-item');
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('misc-invoice-item.delete', {
            parent: 'misc-invoice-item',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'miscInvoice', 'selectedCurrency', function($stateParams, $state, $uibModal, miscInvoice, selectedCurrency) {
                $uibModal.open({
                    templateUrl: 'app/entities/misc-invoice-item/misc-invoice-item-delete-dialog.html',
                    controller: 'MiscInvoiceItemDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: function() {
                            for (var i = 0; i < miscInvoice.miscInvoiceItems.length; i++) {
                                if (miscInvoice.miscInvoiceItems[i].id == $stateParams.id) {
                                    return miscInvoice.miscInvoiceItems[i];
                                }
                            }
                            return null;
                        },
                        miscInvoice: function () {
                            return miscInvoice;
                        }
                    }
                }).result.then(function() {
                    if (!miscInvoice.miscInvoiceItems.length) {
                        selectedCurrency.currency = null;
                        selectedCurrency.hasValidRate = true;
                    }
                    $state.go('misc-invoice-item');
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
