(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('OnlineRegDiscountCodeDeleteController',OnlineRegDiscountCodeDeleteController);

    OnlineRegDiscountCodeDeleteController.$inject = ['$uibModalInstance', 'entity', 'OnlineRegDiscountCode'];

    function OnlineRegDiscountCodeDeleteController($uibModalInstance, entity, OnlineRegDiscountCode) {
        var vm = this;

        vm.discountCode = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            OnlineRegDiscountCode.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
