(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('CongressController', CongressController);

    CongressController.$inject = ['Congress', 'DTOptionsBuilder', 'DTColumnDefBuilder'];

    function CongressController (Congress, DTOptionsBuilder, DTColumnDefBuilder) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions();
        vm.dtOptions.withOption('order', [0, 'desc']);
        vm.dtOptions.withOption('stateSave', true);
        vm.dtColumnDefs = [
            DTColumnDefBuilder.newColumnDef(-1).notSortable(),
            DTColumnDefBuilder.newColumnDef(-2).notSortable()
        ];

        vm.congresses = [];
        vm.openOnlineReg = openOnlineReg;
        vm.getValue = getValue;

        loadAll();

        function loadAll() {
            Congress.query(function(result) {
                vm.congresses = result;
            });
        }

        function openOnlineReg(uuid, currency, language) {
            window.open('/#/registration/online/form/' + uuid + '/' + currency.toLowerCase() + '/' + language, '_blank');
        }

        function getValue(inputId) {
            return document.getElementById(inputId).value;
        }
    }
})();
