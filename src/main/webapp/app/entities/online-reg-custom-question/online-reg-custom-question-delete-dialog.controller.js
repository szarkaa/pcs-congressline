(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('OnlineRegCustomQuestionDeleteController',OnlineRegCustomQuestionDeleteController);

    OnlineRegCustomQuestionDeleteController.$inject = ['$uibModalInstance', 'entity', 'OnlineRegCustomQuestion'];

    function OnlineRegCustomQuestionDeleteController($uibModalInstance, entity, OnlineRegCustomQuestion) {
        var vm = this;

        vm.onlineRegCustomQuestion = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            OnlineRegCustomQuestion.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
