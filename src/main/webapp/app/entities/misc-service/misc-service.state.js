(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('misc-service', {
            parent: 'administration',
            url: '/misc-service',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN'],
                pageTitle: 'pcsApp.miscService.home.title'
            },
            views: {
                'content@base': {
                    templateUrl: 'app/entities/misc-service/misc-services.html',
                    controller: 'MiscServiceController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('miscService');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('misc-service.new', {
            parent: 'misc-service',
            url: '/new',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'CongressSelector', function($stateParams, $state, $uibModal, CongressSelector) {
                $uibModal.open({
                    templateUrl: 'app/entities/misc-service/misc-service-dialog.html',
                    controller: 'MiscServiceDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                id: null,
                                name: null,
                                description: null,
                                measure: null,
                                price: null,
                                vatInfo: null,
                                currency: null,
                                congressId: CongressSelector.getSelectedCongress().id
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('misc-service', null, { reload: 'misc-service' });
                }, function() {
                    $state.go('misc-service');
                });
            }]
        })
        .state('misc-service.edit', {
            parent: 'misc-service',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/misc-service/misc-service-dialog.html',
                    controller: 'MiscServiceDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['MiscService', function(MiscService) {
                            return MiscService.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('misc-service', null, { reload: 'misc-service' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('misc-service.delete', {
            parent: 'misc-service',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/misc-service/misc-service-delete-dialog.html',
                    controller: 'MiscServiceDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['MiscService', function(MiscService) {
                            return MiscService.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('misc-service', null, { reload: 'misc-service' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
