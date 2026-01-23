(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('room', {
            parent: 'congress-hotel',
            url: '/{congressHotelId}/rooms',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'pcsApp.room.home.title'
            },
            views: {
                'content@base': {
                    templateUrl: 'app/entities/room/rooms.html',
                    controller: 'RoomController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                congressHotel: ['$stateParams', 'CongressHotel', function ($stateParams, CongressHotel) {
                    return CongressHotel.get({id: $stateParams.congressHotelId}).$promise;
                }],
                rooms: ['$stateParams', 'Room', function ($stateParams, Room) {
                    return Room.queryByCongressHotelId({id: $stateParams.congressHotelId});
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('room');
                    $translatePartialLoader.addPart('onlineVisibility');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('room.new', {
            parent: 'room',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'congressHotel', function($stateParams, $state, $uibModal, congressHotel) {
                $uibModal.open({
                    templateUrl: 'app/entities/room/room-dialog.html',
                    controller: 'RoomDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                id: null,
                                roomType: null,
                                bed: null,
                                quantity: null,
                                reserved: 0,
                                price: null,
                                onlineLabel: null,
                                onlineVisibility: 'VISIBLE',
                                onlineExternalLink: null,
                                onlineExternalEmail: null,
                                vatInfoId: null,
                                currencyId: null,
                                congressHotelId: $stateParams.congressHotelId
                            };
                        },
                        congressHotel: function () {
                            return congressHotel;
                        }
                    }
                }).result.then(function() {
                    $state.go('room', null, { reload: 'room' });
                }, function() {
                    $state.go('room');
                });
            }]
        })
        .state('room.edit', {
            parent: 'room',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'congressHotel', function($stateParams, $state, $uibModal, congressHotel) {
                $uibModal.open({
                    templateUrl: 'app/entities/room/room-dialog.html',
                    controller: 'RoomDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Room', function (Room) {
                            return Room.get({id: $stateParams.id}).$promise;
                        }],
                        congressHotel: function () {
                            return congressHotel;
                        }
                    },
                }).result.then(function() {
                    $state.go('room', null, { reload: 'room' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('room.delete', {
            parent: 'room',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/room/room-delete-dialog.html',
                    controller: 'RoomDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Room', function(Room) {
                            return Room.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('room', null, { reload: 'room' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
