(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('RegistrationSearchController',RegistrationSearchController);

    RegistrationSearchController.$inject = ['$scope', '$timeout', '$uibModalInstance', '$state', 'registrationArray', 'RegIdStorage', 'DTOptionsBuilder', 'DTColumnDefBuilder'];

    function RegistrationSearchController($scope, $timeout, $uibModalInstance, $state, registrationArray, RegIdStorage, DTOptionsBuilder, DTColumnDefBuilder) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions()
            .withDOM('<"html5buttons"B>ltfgitp')
            .withOption('rowCallback', rowCallback)
            .withOption('initComplete', initComplete)
            .withButtons([
            ]);

        vm.dtColumnDefs = [
        ];

        function initComplete () {
            console.log('initComplete');
            angular.element('#search-datatable_filter label input').focus();
        }

        function doubleClickHandler(info) {
            vm.search(info[0]);
        }

        function rowCallback(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
            // Unbind first in order to avoid any duplicate handler (see https://github.com/l-lin/angular-datatables/issues/87)
            $('td', nRow).unbind('click');
            $('td', nRow).bind('click', function() {
                $scope.$apply(function() {
                    doubleClickHandler(aData);
                });
            });
            return nRow;
        }

        vm.registrationArray = registrationArray;
        vm.search = search;
        vm.clear = clear;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function search (regId) {
            for (var i = 0; i < vm.registrationArray.length; i++) {
                if (vm.registrationArray[i].regId == regId) {
                    $state.go('registration', {registrationId: vm.registrationArray[i].id});
                }
            }
            $uibModalInstance.close(true);
        }
    }
})();
