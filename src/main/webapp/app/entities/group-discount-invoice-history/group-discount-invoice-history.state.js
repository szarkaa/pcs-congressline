(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('group-discount-invoice-history', {
            parent: 'entity',
            url: '/group-discount-invoice-history',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'pcsApp.groupDiscountInvoiceHistory.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/group-discount-invoice-history/group-discount-invoice-histories.html',
                    controller: 'GroupDiscountInvoiceHistoryController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('groupDiscountInvoiceHistory');
                    $translatePartialLoader.addPart('chargeableItemType');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('group-discount-invoice-history-detail', {
            parent: 'entity',
            url: '/group-discount-invoice-history/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'pcsApp.groupDiscountInvoiceHistory.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/group-discount-invoice-history/group-discount-invoice-history-detail.html',
                    controller: 'GroupDiscountInvoiceHistoryDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('groupDiscountInvoiceHistory');
                    $translatePartialLoader.addPart('chargeableItemType');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'GroupDiscountInvoiceHistory', function($stateParams, GroupDiscountInvoiceHistory) {
                    return GroupDiscountInvoiceHistory.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'group-discount-invoice-history',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('group-discount-invoice-history-detail.edit', {
            parent: 'group-discount-invoice-history-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/group-discount-invoice-history/group-discount-invoice-history-dialog.html',
                    controller: 'GroupDiscountInvoiceHistoryDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['GroupDiscountInvoiceHistory', function(GroupDiscountInvoiceHistory) {
                            return GroupDiscountInvoiceHistory.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('group-discount-invoice-history.new', {
            parent: 'group-discount-invoice-history',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/group-discount-invoice-history/group-discount-invoice-history-dialog.html',
                    controller: 'GroupDiscountInvoiceHistoryDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                chargeableItemId: null,
                                chargeableItemType: null,
                                df: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('group-discount-invoice-history', null, { reload: 'group-discount-invoice-history' });
                }, function() {
                    $state.go('group-discount-invoice-history');
                });
            }]
        })
        .state('group-discount-invoice-history.edit', {
            parent: 'group-discount-invoice-history',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/group-discount-invoice-history/group-discount-invoice-history-dialog.html',
                    controller: 'GroupDiscountInvoiceHistoryDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['GroupDiscountInvoiceHistory', function(GroupDiscountInvoiceHistory) {
                            return GroupDiscountInvoiceHistory.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('group-discount-invoice-history', null, { reload: 'group-discount-invoice-history' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('group-discount-invoice-history.delete', {
            parent: 'group-discount-invoice-history',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/group-discount-invoice-history/group-discount-invoice-history-delete-dialog.html',
                    controller: 'GroupDiscountInvoiceHistoryDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['GroupDiscountInvoiceHistory', function(GroupDiscountInvoiceHistory) {
                            return GroupDiscountInvoiceHistory.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('group-discount-invoice-history', null, { reload: 'group-discount-invoice-history' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
