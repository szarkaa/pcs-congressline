(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('room-reservation-by-room', {
                parent: 'report',
                url: '/room-reservation-by-room',
                data: {
                    authorities: ['ROLE_ADVANCED_USER','ROLE_ADMIN'],
                    pageTitle: 'pcsApp.roomReservationByRoom.home.title'
                },
                views: {
                    'content@base': {
                        templateUrl: 'app/entities/room-reservation-by-room/room-reservation-by-rooms.html',
                        controller: 'RoomReservationByRoomController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    listFilter: ['RoomReservationByRoomFilter', function (RoomReservationByRoomFilter) {
                        return RoomReservationByRoomFilter.getRoomReservationByRoomFilter();
                    }],
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('roomReservationByRoom');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('room-reservation-by-room.selection', {
                parent: 'room-reservation-by-room',
                url: '/select',
                data: {
                    authorities: ['ROLE_ADVANCED_USER','ROLE_ADMIN']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', 'listFilter', function ($stateParams, $state, $uibModal, listFilter) {
                    $uibModal.open({
                        templateUrl: 'app/entities/room-reservation-by-room/room-reservation-by-room-selection-dialog.html',
                        controller: 'RoomReservationByRoomSelectionDialogController',
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
