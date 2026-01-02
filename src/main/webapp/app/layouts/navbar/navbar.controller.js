(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('NavbarController', NavbarController);

    NavbarController.$inject = ['$state', 'Auth', 'Principal', 'ProfileService', 'CongressSelector'];

    function NavbarController ($state, Auth, Principal, ProfileService, CongressSelector) {
        var vm = this;
        vm.$state = $state;
        vm.isCongressSelected = isCongressSelected;

        ProfileService.getProfileInfo().then(function(response) {
            vm.inProduction = response.inProduction;
            vm.swaggerEnabled = response.swaggerEnabled;
        });

        Principal.identity().then(function(account) {
            vm.account = account;
            vm.isAuthenticated = Principal.isAuthenticated;
        });

        function isCongressSelected () {
            return CongressSelector.getSelectedCongress() != null;
        }

    }
})();
