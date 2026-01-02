(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('optional-service', {
            parent: 'administration',
            url: '/optional-service',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'pcsApp.optionalService.home.title'
            },
            views: {
                'content@base': {
                    templateUrl: 'app/entities/optional-service/optional-services.html',
                    controller: 'OptionalServiceController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('optionalService');
                    $translatePartialLoader.addPart('onlineVisibility');
                    $translatePartialLoader.addPart('onlineType');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('optional-service.new', {
            parent: 'optional-service',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'CongressSelector', function($stateParams, $state, $uibModal, CongressSelector) {
                $uibModal.open({
                    templateUrl: 'app/entities/optional-service/optional-service-dialog.html',
                    controller: 'OptionalServiceDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                code: null,
                                name: null,
                                startDate: new Date(CongressSelector.getSelectedCongress().startDate),
                                endDate: new Date(CongressSelector.getSelectedCongress().endDate),
                                price: null,
                                maxPerson: null,
                                reserved: 0,
                                onlineLabel: null,
                                onlineOrder: null,
                                onlineVisibility: 'VISIBLE',
                                onlineType: 'NORMAL',
                                id: null,
                                congress: CongressSelector.getSelectedCongress()
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('optional-service', null, { reload: 'optional-service' });
                }, function() {
                    $state.go('optional-service');
                });
            }]
        })
        .state('optional-service.edit', {
            parent: 'optional-service',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/optional-service/optional-service-dialog.html',
                    controller: 'OptionalServiceDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['OptionalService', function(OptionalService) {
                            return OptionalService.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('optional-service', null, { reload: 'optional-service' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('optional-service.delete', {
            parent: 'optional-service',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/optional-service/optional-service-delete-dialog.html',
                    controller: 'OptionalServiceDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['OptionalService', function(OptionalService) {
                            return OptionalService.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('optional-service', null, { reload: 'optional-service' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
