(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('paying-group-item', {
            parent: 'administration',
            url: '/paying-groups/{payingGroupId}/paying-group-items',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN'],
                pageTitle: 'pcsApp.payingGroupItem.home.title'
            },
            views: {
                'content@base': {
                    templateUrl: 'app/entities/paying-group-item/paying-group-items.html',
                    controller: 'PayingGroupItemController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                payingGroup: ['$stateParams', 'PayingGroup', function ($stateParams, PayingGroup) {
                    return PayingGroup.get({id: $stateParams.payingGroupId}).$promise;
                }],
                payingGroupItems: ['$stateParams', 'PayingGroupItem', function ($stateParams, PayingGroupItem) {
                    return PayingGroupItem.queryByPayingGroup({id: $stateParams.payingGroupId});
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('payingGroupItem');
                    $translatePartialLoader.addPart('chargeableItemType');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('paying-group-item.new', {
            parent: 'paying-group-item',
            url: '/new',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/paying-group-item/paying-group-item-dialog.html',
                    controller: 'PayingGroupItemDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                id: null,
                                name: null,
                                amountPercentage: null,
                                amountValue: null,
                                hotelDateFrom: null,
                                hotelDateTo: null,
                                chargeableItemType: null,
                                payingGroupId: $stateParams.payingGroupId
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('paying-group-item', null, { reload: 'paying-group-item' });
                }, function() {
                    $state.go('paying-group-item');
                });
            }]
        })
        .state('paying-group-item.edit', {
            parent: 'paying-group-item',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/paying-group-item/paying-group-item-dialog.html',
                    controller: 'PayingGroupItemDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['PayingGroupItem', function(PayingGroupItem) {
                            return PayingGroupItem.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('paying-group-item', null, { reload: 'paying-group-item' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('paying-group-item.delete', {
            parent: 'paying-group-item',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/paying-group-item/paying-group-item-delete-dialog.html',
                    controller: 'PayingGroupItemDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['PayingGroupItem', function(PayingGroupItem) {
                            return PayingGroupItem.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('paying-group-item', null, { reload: 'paying-group-item' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
