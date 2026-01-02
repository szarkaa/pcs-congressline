(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('OnlineRegCustomAnswerDetailDialogController', OnlineRegCustomAnswerDetailDialogController);

    OnlineRegCustomAnswerDetailDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'OnlineRegCustomAnswerReport'];

    function OnlineRegCustomAnswerDetailDialogController ($timeout, $scope, $stateParams, $uibModalInstance, OnlineRegCustomAnswerReport) {
        var vm = this;

        vm.clear = clear;

        OnlineRegCustomAnswerReport.getAnswersByRegId({id: $stateParams.id}, function(result) {
            vm.customAnswers = result;
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();
