(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('AccPeopleController', AccPeopleController);

    AccPeopleController.$inject = ['$scope', '$state', 'registrationRegistrationType', 'accPeoples', 'DTOptionsBuilder', 'DTColumnDefBuilder'];

    function AccPeopleController ($scope, $state, registrationRegistrationType, accPeoples, DTOptionsBuilder, DTColumnDefBuilder) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions();
        vm.dtColumnDefs = [
            // negative int calculated from the right of the columns
            DTColumnDefBuilder.newColumnDef(-1).notSortable()
        ];

        vm.registrationRegistrationType = registrationRegistrationType;
        vm.accPeoples = accPeoples;
    }
})();
