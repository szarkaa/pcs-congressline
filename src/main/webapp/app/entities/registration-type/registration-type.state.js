(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('registration-type', {
            parent: 'administration',
            url: '/registration-type',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'pcsApp.registrationType.home.title'
            },
            views: {
                'content@base': {
                    templateUrl: 'app/entities/registration-type/registration-types.html',
                    controller: 'RegistrationTypeController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('registrationType');
                    $translatePartialLoader.addPart('registrationTypeType');
                    $translatePartialLoader.addPart('onlineType');
                    $translatePartialLoader.addPart('onlineVisibility');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('registration-type.new', {
            parent: 'registration-type',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'CongressSelector', function($stateParams, $state, $uibModal, CongressSelector) {
                $uibModal.open({
                    templateUrl: 'app/entities/registration-type/registration-type-dialog.html',
                    controller: 'RegistrationTypeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                id: null,
                                code: null,
                                name: null,
                                firstRegFee: null,
                                firstDeadline: null,
                                secondRegFee: null,
                                secondDeadline: null,
                                thirdRegFee: null,
                                registrationType: null,
                                onlineLabel: null,
                                onlineOrder: null,
                                onlineVisibility: 'VISIBLE',
                                onlineType: 'NORMAL',
                                currencyId: null,
                                vatInfoId: null,
                                congressId: CongressSelector.getSelectedCongress().id
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('registration-type', null, { reload: 'registration-type' });
                }, function() {
                    $state.go('registration-type');
                });
            }]
        })
        .state('registration-type.edit', {
            parent: 'registration-type',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/registration-type/registration-type-dialog.html',
                    controller: 'RegistrationTypeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['RegistrationType', function(RegistrationType) {
                            return RegistrationType.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('registration-type', null, { reload: 'registration-type' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('registration-type.delete', {
            parent: 'registration-type',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/registration-type/registration-type-delete-dialog.html',
                    controller: 'RegistrationTypeDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['RegistrationType', function(RegistrationType) {
                            return RegistrationType.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('registration-type', null, { reload: 'registration-type' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
