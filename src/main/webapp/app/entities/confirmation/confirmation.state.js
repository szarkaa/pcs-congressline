(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

        stateConfig.$inject = ['$stateProvider'];

        function stateConfig($stateProvider) {
            $stateProvider
                .state('confirmation', {
                    parent: 'registration',
                    url: '/confirmation',
                    data: {
                        authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN'],
                        pageTitle: 'pcsApp.registration.confirmation.title'
                    },
                    views: {
                        'content@base': {
                            templateUrl: 'app/entities/confirmation/confirmation.html',
                            controller: 'ConfirmationController',
                            controllerAs: 'vm'
                        }
                    },
                    resolve: {
                        translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                            $translatePartialLoader.addPart('optionalText');
                            $translatePartialLoader.addPart('global');
                            return $translate.refresh();
                        }]
                    }
                })
                .state('confirmation.optional-text-new', {
                    parent: 'confirmation',
                    url: '/optional-text/new',
                    data: {
                        authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
                    },
                    onEnter: ['$stateParams', '$state', '$uibModal', 'registration', 'CongressSelector', function ($stateParams, $state, $uibModal, registration, CongressSelector) {
                        $uibModal.open({
                            templateUrl: 'app/entities/optional-text/optional-text-dialog.html',
                            controller: 'OptionalTextDialogController',
                            controllerAs: 'vm',
                            size: 'lg',
                            backdrop: 'static',
                            resolve: {
                                entity: function () {
                                    return {
                                        name: null,
                                        optionalText: null,
                                        id: null,
                                        congress: CongressSelector.getSelectedCongress()
                                    };
                                }
                            }
                        }).result.then(function (result) {
                                $state.go('confirmation', {registrationId: registration.id}, {reload: true});
                            }, function () {
                                $state.go('confirmation', {registrationId: registration.id});
                            })
                    }]
                });
        }
})();
