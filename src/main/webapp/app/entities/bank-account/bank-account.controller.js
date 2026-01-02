(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('BankAccountController', BankAccountController);

    BankAccountController.$inject = ['$scope', '$state', 'BankAccount', 'DTOptionsBuilder', 'DTColumnDefBuilder'];

    function BankAccountController ($scope, $state, BankAccount, DTOptionsBuilder, DTColumnDefBuilder) {
        var vm = this;
        $scope.dtOptions = DTOptionsBuilder.newOptions();
        $scope.dtOptions.withOption('stateSave', true);
        $scope.dtColumnDefs = [
            // negative int calculated from the right of the columns
            DTColumnDefBuilder.newColumnDef(-1).notSortable()
        ];

        vm.bankAccounts = [];

        loadAll();

        function loadAll() {
            BankAccount.query(function(result) {
                vm.bankAccounts = result;
            });
        }
    }
})();
