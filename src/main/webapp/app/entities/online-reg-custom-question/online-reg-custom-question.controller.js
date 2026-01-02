(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('OnlineRegCustomQuestionController', OnlineRegCustomQuestionController);

    OnlineRegCustomQuestionController.$inject = ['$state', '$stateParams', 'Congress', 'congress', 'DTOptionsBuilder', 'DTColumnDefBuilder'];

    function OnlineRegCustomQuestionController ($state, $stateParams, Congress, congress, DTOptionsBuilder, DTColumnDefBuilder) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions();
        vm.dtOptions.withOption('order', [1, 'asc']);
        vm.dtOptions.withOption('stateSave', true);
        vm.dtColumnDefs = [
            // negative int calculated from the right of the columns
            DTColumnDefBuilder.newColumnDef(-1).notSortable()
        ];

        vm.congress = congress;
        vm.questions = [];

        loadAll();

        function loadAll() {
            Congress.getOnlineRegCustomQuestions({id: $stateParams.congressId}, function(result) {
                vm.questions = result;
            });
        }
    }
})();
