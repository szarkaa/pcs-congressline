(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('CongressDialogController', CongressDialogController);

    CongressDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Congress', 'Country', 'Currency', 'BankAccount', 'User'];

    function CongressDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Congress, Country, Currency, BankAccount, User) {
        var vm = this;

        vm.congress = {
            id: entity.id,
            meetingCode: entity.meetingCode,
            name: entity.name,
            startDate: entity.startDate,
            endDate: entity.endDate,
            contactPerson: entity.contactPerson,
            contactEmail: entity.contactEmail,
            website: entity.website,
            programNumber: entity.programNumber,
            defaultCountryId: entity.defaultCountry ? entity.defaultCountry.id : null,
            additionalBillingTextHu: entity.additionalBillingTextHu,
            additionalBillingTextEn: entity.additionalBillingTextEn,
            archive: entity.archive,
            migratedFromCongressCode: entity.migratedFromCongressCode,
            currencies: entity.currencies,
            onlineRegCurrencies: entity.onlineRegCurrencies,
            bankAccounts: entity.bankAccounts
        };

        vm.congressFromCopyWp;
        vm.datePickerOpenStatus = {};
        vm.countries = Country.query();
        vm.currencies = Currency.query();
        vm.congresses = Congress.query();
        vm.bankAccounts = BankAccount.query();
        vm.users = User.query();
        vm.bankAccountByCurrency = {};

        vm.clear = clear;
        vm.openCalendar = openCalendar;
        vm.setCongressEndDate = setCongressEndDate;
        vm.save = save;

        initBankAccountsForCurrencies();

        $timeout(function (){
            angular.element('.form-group:eq(0)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function initBankAccountsForCurrencies () {
            for (var i = 0; i < vm.congress.currencies.length; i++) {
                vm.bankAccountByCurrency[vm.congress.currencies[i].currency] = null;
                for (var j = 0; j < vm.congress.bankAccounts.length; j++) {
                    if (vm.congress.bankAccounts[j].currency.currency === vm.congress.currencies[i].currency) {
                        vm.bankAccountByCurrency[vm.congress.currencies[i].currency] = vm.congress.bankAccounts[j];
                    }
                }
            }
        }

        function preProcessBankAccountsForCurrencies () {
            vm.congress.bankAccounts = [];
            for (var prop in vm.bankAccountByCurrency) {
                if (vm.bankAccountByCurrency.hasOwnProperty(prop) || vm.bankAccountByCurrency[prop]) {
                    vm.congress.bankAccounts.push(vm.bankAccountByCurrency[prop]);
                }
            }
        }

        function save () {
            vm.isSaving = true;
            preProcessBankAccountsForCurrencies();

            vm.congress.currencyIds = vm.congress.currencies ? vm.congress.currencies.map(c => c.id) : [];
            // delete vm.congress.currencies;
            vm.congress.onlineRegCurrencyIds = vm.congress.onlineRegCurrencies ? vm.congress.onlineRegCurrencies.map(c => c.id) : [];
            // delete vm.congress.onlineRegCurrencies;
            vm.congress.bankAccountIds = vm.congress.bankAccounts ? vm.congress.bankAccounts.map(c => c.id) : [];
            // delete vm.congress.bankAccounts;
            if (vm.congress.id !== null) {
                Congress.update(vm.congress, onSaveSuccess, onSaveError);
            } else {
                Congress.save(vm.congress, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:congressUpdate', result);
            if (vm.congressFromCopyWp) {
                Congress.migrateItems({from: vm.congressFromCopyWp.id, to: result.id},
                    function () {
                        vm.congressFromCopyWp = null;
                    }, function () {
                        vm.congressFromCopyWp = null;
                    });
            }
            $uibModalInstance.close(result);
            //vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.startDate = false;
        vm.datePickerOpenStatus.endDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }

        function setCongressEndDate() {
            if (!vm.congress.endDate) {
                vm.congress.endDate = vm.congress.startDate;
            }
        }
    }
})();
