(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('RoomController', RoomController);

    RoomController.$inject = ['$scope', '$state', 'Room', 'DTOptionsBuilder', 'DTColumnDefBuilder', 'congressHotel', 'rooms'];

    function RoomController ($scope, $state, Room, DTOptionsBuilder, DTColumnDefBuilder, congressHotel, rooms) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions();
        vm.dtOptions.withOption('stateSave', true);
        vm.dtColumnDefs = [
            // negative int calculated from the right of the columns
            DTColumnDefBuilder.newColumnDef(-1).notSortable()
        ];

        vm.congressHotel = congressHotel;
        vm.rooms = rooms;

        //loadAll();

        function loadAll() {
            Room.queryByCongressHotelId({id: vm.congressHotel.id}, function(result) {
                vm.rooms = result;
            });
        }
    }
})();
