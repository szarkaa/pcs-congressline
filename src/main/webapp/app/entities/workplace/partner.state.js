(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('partner', {
                parent: 'maintenance',
                url: '/partners',
                data: {
                    authorities: ['ROLE_ADVANCED_USER','ROLE_ADMIN'],
                    pageTitle: 'pcsApp.partner.home.title'
                },
                views: {
                    'content@base': {
                        templateUrl: 'app/entities/workplace/workplaces.html',
                        controller: 'WorkplaceController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    isPartner: function() { return true; },
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('partner');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('partner.new', {
                parent: 'partner',
                url: '/new',
                data: {
                    authorities: ['ROLE_ADVANCED_USER','ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', 'CongressSelector', function($stateParams, $state, $uibModal, CongressSelector) {
                    $uibModal.open({
                        templateUrl: 'app/entities/workplace/workplace-dialog.html',
                        controller: 'WorkplaceDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    id: null,
                                    name: null,
                                    countryId: null,
                                    vatRegNumber: null,
                                    department: null,
                                    zipCode: null,
                                    city: null,
                                    street: null,
                                    phone: null,
                                    fax: null,
                                    email: null,
                                    congressId: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                            $state.go('partner', null, { reload: 'partner' });
                        }, function() {
                            $state.go('partner');
                        })
                }]
            })
            .state('partner.edit', {
                parent: 'partner',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_ADVANCED_USER','ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/workplace/workplace-dialog.html',
                        controller: 'WorkplaceDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: ['Workplace', function(Workplace) {
                                return Workplace.get({id : $stateParams.id}).$promise;
                            }]
                        }
                    }).result.then(function(result) {
                            $state.go('partner', null, { reload: 'partner' });
                        }, function() {
                            $state.go('^');
                        })
                }]
            })
            .state('partner.delete', {
                parent: 'partner',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_ADVANCED_USER','ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/workplace/workplace-delete-dialog.html',
                        controller: 'WorkplaceDeleteController',
                        controllerAs: 'vm',
                        size: 'md',
                        resolve: {
                            entity: ['Workplace', function(Workplace) {
                                return Workplace.get({id : $stateParams.id}).$promise;
                            }]
                        }
                    }).result.then(function(result) {
                            $state.go('partner', null, { reload: true });
                        }, function() {
                            $state.go('^');
                        })
                }]
            });
    }

})();
