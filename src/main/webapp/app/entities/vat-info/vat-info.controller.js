(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('VatInfoController', VatInfoController);

    VatInfoController.$inject = ['$scope', '$state', '$stateParams', 'VatInfo', 'congressSpecific', 'CongressSelector'];

    function VatInfoController ($scope, $state, $stateParams, VatInfo, congressSpecific, CongressSelector) {
        var vm = this;
        vm.congressSpecific = $stateParams.congressSpecific;
        vm.selectedCongressMeetingCode = vm.congressSpecific && CongressSelector.getSelectedCongress() ? CongressSelector.getSelectedCongress().meetingCode : "";
        vm.vatInfos = [];

        vm.vatInfos = [];
        vm.congressSpecific = congressSpecific;

        loadAll();

        function loadAll() {
            if (vm.congressSpecific) {
                VatInfo.queryByCongress({id: CongressSelector.getSelectedCongress().id}, function(result) {
                    vm.vatInfos = result;
                });
            }
            else {
                VatInfo.queryByCongress({id: 0}, function(result) {
                    vm.vatInfos = result;
                });
            }
        }
    }
})();
