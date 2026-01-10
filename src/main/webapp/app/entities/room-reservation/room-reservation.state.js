(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('room-reservation', {
            abstract: true,
            parent: 'registration',
            resolve: {
                registrationCurrency: ['registrationRegistrationTypes', 'roomReservations', 'orderedOptionalServices', function (registrationRegistrationTypes, roomReservations, orderedOptionalServices) {
                    if (registrationRegistrationTypes.length) {
                        return registrationRegistrationTypes[0].chargeableItemCurrency;
                    }

                    if (roomReservations.length) {
                        return roomReservations[0].chargeableItemCurrency;
                    }

                    if (orderedOptionalServices.length) {
                        return orderedOptionalServices[0].chargeableItemCurrency;
                    }
                    return null;
                }]
            }
        })
        .state('room-reservation.new', {
            parent: 'room-reservation',
            url: '/room-reservation/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'registration', 'CongressSelector', 'registrationCurrency', function($stateParams, $state, $uibModal, registration, CongressSelector, registrationCurrency) {
                $uibModal.open({
                    templateUrl: 'app/entities/room-reservation/room-reservation-dialog.html',
                    controller: 'RoomReservationDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                roomId: null,
                                shared: false,
                                arrivalDate: new Date(CongressSelector.getSelectedCongress().startDate),
                                departureDate: new Date(CongressSelector.getSelectedCongress().endDate),
                                payingGroupItemId: null,
                                id: null,
                                registrationId: registration.id,
                                '@class': 'hu.congressline.pcs.domain.RoomReservation'
                            };
                        },
                        registrationCurrency: function () {
                            return registrationCurrency;
                        },
                        invoicedChargeableItemIds: function () {
                            return [];
                        }
                    }
                }).result.then(function(result) {
                        $state.go('registration', {registrationId: registration.id}, { reload: true });
                    }, function() {
                        $state.go('registration', {registrationId: registration.id});
                    })
            }]
        })
        .state('room-reservation.shared-selection', {
            parent: 'room-reservation',
            url: '/room-reservation/shared-selection',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'registration', 'RoomReservation', 'CongressSelector', 'registrationCurrency',
                function($stateParams, $state, $uibModal, registration, RoomReservation, CongressSelector, registrationCurrency) {
                    $uibModal.open({
                        templateUrl: 'app/entities/room-reservation/shared-room-reservation-dialog.html',
                        controller: 'SharedRoomReservationDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            roomReservations: ['RoomReservation', 'CongressSelector', function(RoomReservation, CongressSelector) {
                                return RoomReservation.queryVMForSharedRoomReservations({congressId: CongressSelector.getSelectedCongress().id, registrationId: $stateParams.registrationId});
                            }],
                            registrationCurrency: function () {
                                return registrationCurrency;
                            }
                        }
                    }).result.then(function(result) {
                            $state.go('registration', {registrationId: registration.id}, { reload: true });
                        }, function() {
                            $state.go('registration', {registrationId: registration.id});
                        })
                }]
        })
        .state('room-reservation.edit', {
            parent: 'room-reservation',
            url: '/room-reservation/{rrrId}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'registration', 'registrationCurrency', function($stateParams, $state, $uibModal, registration, registrationCurrency) {
                $uibModal.open({
                    templateUrl: 'app/entities/room-reservation/room-reservation-dialog.html',
                    controller: 'RoomReservationDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['RoomReservation', function(RoomReservation) {
                            return RoomReservation.get({id : $stateParams.rrrId, registrationId: $stateParams.registrationId}).$promise;
                        }],
                        invoicedChargeableItemIds: ['$stateParams', 'Invoice', function ($stateParams, Invoice) {
                            return Invoice.getInvoicedChargeableItemIdsByRegistrationId({id: $stateParams.registrationId});
                        }],
                        registrationCurrency: function () {
                            return registrationCurrency;
                        }
                    }
                }).result.then(function(result) {
                        $state.go('registration', {registrationId: registration.id}, { reload: true });
                    }, function() {
                        $state.go('registration', {registrationId: registration.id});
                    })
            }]
        })
        .state('room-reservation.delete', {
            parent: 'room-reservation',
            url: '/room-reservation/{rrrId}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'registration', function($stateParams, $state, $uibModal, registration) {
                $uibModal.open({
                    templateUrl: 'app/entities/room-reservation/room-reservation-delete-dialog.html',
                    controller: 'RoomReservationDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['RoomReservation', function(RoomReservation) {
                            return RoomReservation.get({id : $stateParams.rrrId, registrationId: $stateParams.registrationId});
                        }]
                    }
                }).result.then(function(result) {
                        $state.go('registration', {registrationId: registration.id}, { reload: true });
                    }, function() {
                        $state.go('registration', {registrationId: registration.id});
                    })
            }]
        });
    }

})();
