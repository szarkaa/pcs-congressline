(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('workplace', {
                parent: 'administration',
                url: '/workplace',
                data: {
                    authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN'],
                    pageTitle: 'pcsApp.workplace.home.title'
                },
                views: {
                    'content@base': {
                        templateUrl: 'app/entities/workplace/workplaces.html',
                        controller: 'WorkplaceController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    isPartner: function() { return false; },
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('workplace');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('workplace.new', {
                parent: 'workplace',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', 'CongressSelector', function ($stateParams, $state, $uibModal, CongressSelector) {
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
                                    vatRegNumber: null,
                                    countryId: null,
                                    department: null,
                                    zipCode: null,
                                    city: null,
                                    street: null,
                                    phone: null,
                                    fax: null,
                                    email: null,
                                    congressId: CongressSelector.getSelectedCongress().id
                                };
                            }
                        }
                    }).result.then(function () {
                            $state.go('workplace', null, {reload: 'workplace'});
                        }, function () {
                            $state.go('workplace');
                        });
                }]
            })
            .state('workplace.edit', {
                parent: 'workplace',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/workplace/workplace-dialog.html',
                        controller: 'WorkplaceDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: ['Workplace', function (Workplace) {
                                return Workplace.get({id: $stateParams.id}).$promise;
                            }]
                        }
                    }).result.then(function () {
                            $state.go('workplace', null, {reload: 'workplace'});
                        }, function () {
                            $state.go('^');
                        });
                }]
            })
            .state('workplace.delete', {
                parent: 'workplace',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/workplace/workplace-delete-dialog.html',
                        controller: 'WorkplaceDeleteController',
                        controllerAs: 'vm',
                        size: 'md',
                        resolve: {
                            entity: ['Workplace', function (Workplace) {
                                return Workplace.get({id: $stateParams.id}).$promise;
                            }]
                        }
                    }).result.then(function () {
                            $state.go('workplace', null, {reload: 'workplace'});
                        }, function () {
                            $state.go('^');
                        });
                }]
            })
            .state('workplace.merge', {
                parent: 'workplace',
                url: '/{id}/merge',
                data: {
                    authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN'],
                    pageTitle: 'pcsApp.workplace.home.title'
                },
                views: {
                    'content@base': {
                        templateUrl: 'app/entities/workplace/merge-workplaces.html',
                        controller: 'WorkplaceMergeController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    entity: ['Workplace', '$stateParams', function (Workplace, $stateParams) {
                        return Workplace.get({id: $stateParams.id}).$promise;
                    }],
                    selectedWorkplaces: function () {
                        return {};
                    }
                }
            })
            .state('workplace.merge.confirm', {
                parent: 'workplace.merge',
                url: '/confirm',
                data: {
                    authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN'],
                    pageTitle: 'pcsApp.workplace.home.title'
                },
                onEnter: ['$stateParams', '$state', '$uibModal', 'selectedWorkplaces', function ($stateParams, $state, $uibModal, selectedWorkplaces) {
                    $uibModal.open({
                        templateUrl: 'app/entities/workplace/workplace-merge-confirm-dialog.html',
                        controller: 'WorkplaceMergeConfirmController',
                        controllerAs: 'vm',
                        size: 'md',
                        resolve: {
                            entity: ['Workplace', function (Workplace) {
                                return Workplace.get({id: $stateParams.id}).$promise;
                            }],
                            selectedWorkplaces: function () {
                                return selectedWorkplaces;
                            }
                        }
                    }).result.then(function (result) {
                            $state.go('workplace', null, {reload: true});
                        }, function () {
                            $state.go('^');
                        })
                }]
            });
    }

})();
