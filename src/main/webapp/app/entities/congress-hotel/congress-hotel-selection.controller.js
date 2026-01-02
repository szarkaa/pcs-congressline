(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('CongressHotelSelectionController', CongressHotelSelectionController);

    CongressHotelSelectionController.$inject = ['$scope', '$rootScope', '$stateParams', 'CongressHotel', 'CongressSelector', 'Hotel', 'DTOptionsBuilder', 'DTColumnDefBuilder'];

    function CongressHotelSelectionController ($scope, $rootScope, $stateParams, CongressHotel, CongressSelector, Hotel, DTOptionsBuilder, DTColumnDefBuilder) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions();
        vm.dtOptions.withOption('order', [1, 'asc']);
        vm.dtOptions.withOption('stateSave', true);
        vm.dtColumnDefs = [
            DTColumnDefBuilder.newColumnDef(0).notSortable()
        ];

        vm.selectedHotels = {};
        vm.hotels = [];
        vm.congressHotels = [];
        vm.loadAllHotel = loadAllHotel;
        vm.loadAllCongressHotel = loadAllCongressHotel;
        vm.toggleHotelSelection = toggleHotelSelection;

        function loadAllCongressHotel() {
            CongressHotel.queryByCongress({ id: CongressSelector.getSelectedCongress().id }, function(result) {
                vm.congressHotels = result;
                initSelectedHotels();
            });
        }

        function loadAllHotel() {
            Hotel.query(function(result) {
                vm.hotels = result;
                initSelectedHotels();
            });
        }

        vm.loadAllCongressHotel();
        vm.loadAllHotel();

        function toggleHotelSelection(hotelId) {
            if (isHotelIdSelected(hotelId)) {
                CongressHotel.delete(getCongressHotel(CongressSelector.getSelectedCongress().id, hotelId),
                    function (result) {
                        removeCongressHotel(hotelId);
                        vm.selectedHotels[hotelId.toString()] = false;
                    },
                    function (result) {
                        vm.selectedHotels[hotelId.toString()] = true;
                    });
            } else {
                CongressHotel.save({id: null, congress: { id: CongressSelector.getSelectedCongress().id }, hotel: { id: hotelId }},
                    function (result) {
                        vm.congressHotels.push(result);
                        vm.selectedHotels[hotelId.toString()] = true;
                    },
                    function (result) {
                        vm.selectedHotels[hotelId.toString()] = false;
                    });
            }
        }

        function getCongressHotel(congressId, hotelId) {
            for (var i = 0; i < vm.congressHotels.length; i++) {
                if (vm.congressHotels[i].congress.id === congressId && vm.congressHotels[i].hotel.id === hotelId) {
                    return vm.congressHotels[i];
                }
            }
            return null;
        }

        function removeCongressHotel(hotelId) {
            for (var i = 0; i < vm.congressHotels.length; i++) {
                if (vm.congressHotels[i].hotel.id === hotelId) {
                    vm.congressHotels.splice(i, 1);
                    break;
                }
            }
        }

        function isHotelIdSelected(hotelId) {
            for (var i = 0; i < vm.congressHotels.length; i++) {
                if (vm.congressHotels[i].hotel.id === hotelId) {
                    return true;
                }
            }
            return false;
        }

        function initSelectedHotels() {
            var i, hotelId;
            for (i = 0; i < vm.hotels.length; i++) {
                hotelId = vm.hotels[i].id;
                vm.selectedHotels[hotelId.toString()] = false;
            }

            for (i = 0; i < vm.congressHotels.length; i++) {
                hotelId = vm.congressHotels[i].hotel.id;
                vm.selectedHotels[hotelId.toString()] = true;
            }
        }
    }
})();
