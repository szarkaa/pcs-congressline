(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('paying-group', {
            parent: 'administration',
            url: '/paying-group',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'pcsApp.payingGroup.home.title'
            },
            views: {
                'content@base': {
                    templateUrl: 'app/entities/paying-group/paying-groups.html',
                    controller: 'PayingGroupController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('payingGroup');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('paying-group.new', {
            parent: 'paying-group',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'CongressSelector', function($stateParams, $state, $uibModal, CongressSelector) {
                $uibModal.open({
                    templateUrl: 'app/entities/paying-group/paying-group-dialog.html',
                    controller: 'PayingGroupDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                zipCode: null,
                                city: null,
                                street: null,
                                contactName: null,
                                email: null,
                                phone: null,
                                fax: null,
                                taxNumber: null,
                                id: null,
                                congress: CongressSelector.getSelectedCongress()
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('paying-group', null, { reload: 'paying-group' });
                }, function() {
                    $state.go('paying-group');
                });
            }]
        })
        .state('paying-group.edit', {
            parent: 'paying-group',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/paying-group/paying-group-dialog.html',
                    controller: 'PayingGroupDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['PayingGroup', function(PayingGroup) {
                            return PayingGroup.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('paying-group', null, { reload: 'paying-group' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('paying-group.delete', {
            parent: 'paying-group',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/paying-group/paying-group-delete-dialog.html',
                    controller: 'PayingGroupDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['PayingGroup', function(PayingGroup) {
                            return PayingGroup.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('paying-group', null, { reload: 'paying-group' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
