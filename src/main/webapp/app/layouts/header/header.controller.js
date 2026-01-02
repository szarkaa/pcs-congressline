(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('HeaderController', HeaderController);

    HeaderController.$inject = ['$state', 'Auth', 'Principal', 'ProfileService', 'CongressSelector', 'RegIdStorage', 'User', 'GroupDiscountItemFilter'];

    function HeaderController ($state, Auth, Principal, ProfileService, CongressSelector, RegIdStorage, User, GroupDiscountItemFilter) {
        var vm = this;

        vm.isAuthenticated = Principal.isAuthenticated;

        ProfileService.getProfileInfo().then(function(response) {
            vm.inProduction = response.inProduction;
            vm.swaggerEnabled = response.swaggerEnabled;
        });

        vm.logout = logout;
        vm.$state = $state;
        vm.selectedCongress = CongressSelector.getSelectedCongress();
        vm.selectCongress = selectCongress;
        vm.congresses = [];

        function logout() {
            CongressSelector.setSelectedCongress(null);
            RegIdStorage.setLastRegId(null);
            Auth.logout();
            $state.go('login');
        }

        Principal.identity().then(function (account) {
            User.get({login: account.login}, function(user) {
                vm.congresses = user.congresses;
                if (vm.congresses) {
                    vm.congresses.sort(function (c1, c2) {
                        if (c1.meetingCode < c2.meetingCode) {
                            return -1;
                        }
                        else if (c1.meetingCode > c2.meetingCode) {
                            return 1;
                        }
                        else {
                            return 0;
                        }
                    });
                }
            });
        });

        function selectCongress(congress) {
            vm.selectedCongress = congress;
            CongressSelector.setSelectedCongress(congress);
            RegIdStorage.setLastRegId(null);
            GroupDiscountItemFilter.setGroupDiscountItemFilter(null);
            $state.go('home');
        }
    }
})();
