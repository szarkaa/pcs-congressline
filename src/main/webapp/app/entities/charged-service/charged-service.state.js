(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('charged-service', {
            abstract: true,
            parent: 'registration'
        })
        .state('charged-service.new', {
            parent: 'charged-service',
            url: '/charged-service/new',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'registration',
                function($stateParams, $state, $uibModal, registration) {
                    $uibModal.open({
                        templateUrl: 'app/entities/charged-service/charged-service-dialog.html',
                        controller: 'ChargedServiceDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    id: null,
                                    paymentMode: null,
                                    paymentType: null,
                                    dateOfPayment: new Date(),
                                    amount: null,
                                    cardType: null,
                                    cardNumber: null,
                                    cardExpirationDate: null,
                                    transactionId: null,
                                    comment: null,
                                    chargeableItemId: null,
                                    registrationId: registration.id
                                };
                            },
                            registrationRegistrationTypes: ['RegistrationRegistrationType', function (RegistrationRegistrationType) {
                                return RegistrationRegistrationType.queryByRegistrationId({id: $stateParams.registrationId});
                            }],
                            roomReservations: ['RoomReservation', function (RoomReservation) {
                                return RoomReservation.queryByRegistrationId({id: $stateParams.registrationId});
                            }],
                            orderedOptionalServices: ['OrderedOptionalService', function (OrderedOptionalService) {
                                return OrderedOptionalService.queryByRegistrationId({id: $stateParams.registrationId});
                            }]
                        }
                    }).result.then(function (result) {
                            $state.go('registration', {registrationId: registration.id}, {reload: true});
                        }, function () {
                            $state.go('registration', {registrationId: registration.id});
                        });
            }]
        })
        .state('charged-service.edit', {
            parent: 'charged-service',
            url: '/charged-service/{chargedServiceId}/edit',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'registration',
                function($stateParams, $state, $uibModal, registration) {
                $uibModal.open({
                    templateUrl: 'app/entities/charged-service/charged-service-dialog.html',
                    controller: 'ChargedServiceDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ChargedService', function (ChargedService) {
                            return ChargedService.get({id: $stateParams.chargedServiceId}).$promise;
                        }],
                        registrationRegistrationTypes: ['RegistrationRegistrationType', function (RegistrationRegistrationType) {
                            return RegistrationRegistrationType.queryByRegistrationId({id: $stateParams.registrationId});
                        }],
                        roomReservations: ['RoomReservation', function (RoomReservation) {
                            return RoomReservation.queryByRegistrationId({id: $stateParams.registrationId});
                        }],
                        orderedOptionalServices: ['OrderedOptionalService', function (OrderedOptionalService) {
                            return OrderedOptionalService.queryByRegistrationId({id: $stateParams.registrationId});
                        }]
                    }
                }).result.then(function (result) {
                        $state.go('registration', {registrationId: registration.id}, {reload: true});
                    }, function () {
                        $state.go('registration', {registrationId: registration.id});
                    });
            }]
        })
        .state('charged-service.delete', {
            parent: 'charged-service',
            url: '/charged-service/{chargedServiceId}/delete',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'registration', function($stateParams, $state, $uibModal, registration) {
                $uibModal.open({
                    templateUrl: 'app/entities/charged-service/charged-service-delete-dialog.html',
                    controller: 'ChargedServiceDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ChargedService', function(ChargedService) {
                            return ChargedService.get({id : $stateParams.chargedServiceId}).$promise;
                        }]
                    }
                }).result.then(function (result) {
                        $state.go('registration', {registrationId: registration.id}, {reload: true});
                    }, function () {
                        $state.go('registration', {registrationId: registration.id});
                    });
            }]
        });
    }

})();
