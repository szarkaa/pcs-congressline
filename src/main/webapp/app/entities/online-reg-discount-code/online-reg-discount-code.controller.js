(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('OnlineRegDiscountCodeController', OnlineRegDiscountCodeController);

    OnlineRegDiscountCodeController.$inject = ['$scope', '$state', '$stateParams', 'OnlineRegDiscountCode', 'DTOptionsBuilder', 'DTColumnDefBuilder', 'congress'];

    function OnlineRegDiscountCodeController ($scope, $state, $stateParams, OnlineRegDiscountCode, DTOptionsBuilder, DTColumnDefBuilder, congress) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions();
        vm.dtOptions.withOption('stateSave', true);
        vm.dtColumnDefs = [
            DTColumnDefBuilder.newColumnDef(-1).notSortable()
        ];

        vm.congress = congress;

        loadAll();

        function loadAll() {
            OnlineRegDiscountCode.queryByCongressId({id: $stateParams.congressId}, function(result) {
                vm.discountCodes = result;
            });
        }
    }
})();
