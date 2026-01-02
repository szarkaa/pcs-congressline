(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('room-reservation-by-participant', {
                parent: 'report',
                url: '/room-reservation-by-participant',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'pcsApp.roomReservationByParticipant.home.title'
                },
                views: {
                    'content@base': {
                        templateUrl: 'app/entities/room-reservation-by-participant/room-reservation-by-participants.html',
                        controller: 'RoomReservationByParticipantController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    listFilter: ['RoomReservationByParticipantFilter', function (RoomReservationByParticipantFilter) {
                        return RoomReservationByParticipantFilter.getRoomReservationByParticipantFilter();
                    }],
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('roomReservationByParticipant');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('room-reservation-by-participant.selection', {
                parent: 'room-reservation-by-participant',
                url: '/select',
                data: {
                    authorities: ['ROLE_USER']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', 'listFilter', function ($stateParams, $state, $uibModal, listFilter) {
                    $uibModal.open({
                        templateUrl: 'app/entities/room-reservation-by-participant/room-reservation-by-participant-selection-dialog.html',
                        controller: 'RoomReservationByParticipantSelectionDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            listFilter: function () {
                                return listFilter;
                            }
                        }
                    });
                }]
            });
    }


})();
