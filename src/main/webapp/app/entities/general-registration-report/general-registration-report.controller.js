(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('GeneralRegistrationReportController', GeneralRegistrationReportController);

    GeneralRegistrationReportController.$inject = ['$scope', '$state', 'reportFilter', 'GeneralRegistrationReport', 'DTOptionsBuilder', 'Confirmation',
        'DTColumnDefBuilder', 'CongressSelector', 'RegistrationType', 'Workplace', 'PayingGroup', 'OptionalService', 'Country', 'CongressHotel', 'B64Encoder'];

    function GeneralRegistrationReportController ($scope, $state, reportFilter, GeneralRegistrationReport, DTOptionsBuilder, Confirmation,
        DTColumnDefBuilder, CongressSelector, RegistrationType, Workplace, PayingGroup, OptionalService, Country, CongressHotel, B64Encoder) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions();
        vm.dtOptions.withOption('order', [3, 'asc']);
        vm.dtOptions.withOption('stateSave', true);
        vm.dtOptions.withOption('scrollY', '500px');
        vm.dtOptions.withOption('scrollX', true);
        vm.dtColumnDefs = [
            DTColumnDefBuilder.newColumnDef(0).notSortable(),
            DTColumnDefBuilder.newColumnDef(1).notSortable()
        ];

        RegistrationType.queryByCongress({id: CongressSelector.getSelectedCongress().id}, function(result) {
            vm.registrationTypes = result;
        });
        Workplace.queryByCongress({id: CongressSelector.getSelectedCongress().id}, function (result) {
            vm.workplaces = result;
        });
        PayingGroup.queryByCongress({id: CongressSelector.getSelectedCongress().id}, function (result) {
            vm.payingGroups = result;
        });
        OptionalService.queryByCongress({id: CongressSelector.getSelectedCongress().id}, function(result) {
            vm.optionalServices = result;
        });
        CongressHotel.queryByCongress({id: CongressSelector.getSelectedCongress().id}, function(result) {
            vm.congressHotels = result;
        });

        vm.positions = null;
        vm.otherDatas = null;
        vm.countries = Country.query();
        vm.reportFilter = reportFilter;
        vm.isFiltered = false;
        vm.selectedRegIds = {};
        vm.allRegIdSelected = false;

        vm.search = search;
        vm.clear = clear;
        vm.toggleRegIdSelection = toggleRegIdSelection;
        vm.toggleAllRegIdSelection = toggleAllRegIdSelection;
        vm.isAnyRegIdSelected = isAnyRegIdSelected;
        vm.getSelectedRegIds = getSelectedRegIds;
        vm.downloadReportXls = downloadReportXls;
        vm.downloadReportXlsWithQRCode = downloadReportXlsWithQRCode;
        vm.getAvailableRegType = getAvailableRegType;
        vm.printConfirmation = printConfirmation;
        vm.showRegistrationDetails = showRegistrationDetails;

        vm.reportList = [];
        vm.regTypeColumnList = [];

        search();

        function clear() {
            vm.reportFilter.regId = null;
            vm.reportFilter.lastName = null;
            vm.reportFilter.firstName = null;
            vm.reportFilter.invoiceName = null;
            vm.reportFilter.email = null;
            vm.reportFilter.accPeopleLastName = null;
            vm.reportFilter.accPeopleFirstName = null;
            vm.reportFilter.position = null;
            vm.reportFilter.otherData = null;
            vm.reportFilter.registrationType = null;
            vm.reportFilter.workplace = null;
            vm.reportFilter.country = null;
            vm.reportFilter.payingGroup = null;
            vm.reportFilter.optionalServices = [];
            vm.reportFilter.congressHotel = null;
            vm.reportFilter.countryNegation = false;
            vm.reportFilter.presenter = null;
            vm.reportFilter.etiquette = null;
            vm.reportFilter.closed = null;
            vm.reportFilter.onSpot = null;
            vm.reportFilter.cancelled = null;
            search();
        }

        function buildReportFilter(reportFilter) {
            return B64Encoder.encode(JSON.stringify({
                regId: reportFilter.regId,
                lastName: reportFilter.lastName,
                firstName: reportFilter.firstName,
                invoiceName: reportFilter.invoiceName,
                email: reportFilter.email,
                position: reportFilter.position,
                otherData: reportFilter.otherData,
                accPeopleLastName: reportFilter.accPeopleLastName,
                accPeopleFirstName: reportFilter.accPeopleFirstName,
                registrationType: reportFilter.registrationType ? reportFilter.registrationType.id : null,
                workplace: reportFilter.workplace ? reportFilter.workplace.id : null,
                payingGroup: reportFilter.payingGroup ? reportFilter.payingGroup.id : null,
                optionalServices: reportFilter.optionalServices && reportFilter.optionalServices.length ? reportFilter.optionalServices.map(o => o.id) : [],
                hotelId: reportFilter.congressHotel ? reportFilter.congressHotel.hotel.id : null,
                country: reportFilter.country ? reportFilter.country.id : null,
                countryNegation: reportFilter.countryNegation,
                presenter: reportFilter.presenter,
                etiquette: reportFilter.etiquette,
                closed: reportFilter.closed,
                onSpot: reportFilter.onSpot,
                cancelled: reportFilter.cancelled,
                congressId: reportFilter.congressId
            }));
        }

        function search() {
            GeneralRegistrationReport.query({
                    query: buildReportFilter(vm.reportFilter)
                },
                function (result) {
                    vm.reportList = result;
                    createFiltersFromReportList();
                    createRegTypeColumns();
                    checkListIsFiltered();
                    clearRegIdSelection();
                }
            );
        }

        function createFiltersFromReportList() {
            if (!vm.positions || !vm.otherDatas) {
                var positionSet = new Set();
                var otherDataSet = new Set();
                for (var i = 0; i < vm.reportList.length; i++) {
                    if (vm.reportList[i].position) {
                        positionSet.add(vm.reportList[i].position);
                    }
                    if (vm.reportList[i].otherData) {
                        otherDataSet.add(vm.reportList[i].otherData);
                    }
                }
                vm.positions = Array.from(positionSet);
                vm.positions.sort();

                vm.otherDatas = Array.from(otherDataSet);
                vm.otherDatas.sort();
            }
        }

        function createRegTypeColumns() {
            var rtSet = new Set();
            for (var i = 0; i < vm.reportList.length; i++) {
                for (var j = 0; j < vm.reportList[i].registrationTypes.length; j++) {
                    var rt = vm.reportList[i].registrationTypes[j];
                    rtSet.add(rt.code);
                }
            }
            vm.regTypeColumnList = Array.from(rtSet);
            vm.regTypeColumnList.sort();
        }

        function getAvailableRegType(grr, regTypeColumn) {
            for (var i = 0; i < grr.registrationTypes.length; i++) {
                if (grr.registrationTypes[i].code == regTypeColumn) {
                    return regTypeColumn;
                }
            }
            return "";
        }

        function printConfirmation(registrationId, countryCode) {
            Confirmation.printConfirmation(createConfirmationForPrinting(registrationId, countryCode, ''));
        }

        function createConfirmationForPrinting(registrationId, countryCode, customConfirmationEmail) {
            var conf = {};
            conf.language = countryCode && countryCode !== 'HU' ? 'en' : 'hu';
            conf.confirmationTitleType = 'CONFIRMATION';
            conf.optionalText = '';
            conf.registrationId = registrationId;
            conf.customConfirmationEmail = customConfirmationEmail;
            conf.ignoredChargeableItemIdList = [];
            conf.ignoredChargedServiceIdList = [];
            return conf;
        }

        function downloadReportXls () {
            window.location.href = '/api/general-registration-report/download-report?query=' + buildReportFilter(vm.reportFilter);
        }

        function downloadReportXlsWithQRCode () {
            window.location.href = '/api/general-registration-report/download-report-with-qrcode?query=' + buildReportFilter(vm.reportFilter);
        }

        function showRegistrationDetails(id) {
            window.open('/#/administration/registrations/' + id, '_blank');
        }

        function checkListIsFiltered() {
            vm.isFiltered = vm.reportFilter.regId ||
            vm.reportFilter.lastName ||
            vm.reportFilter.firstName||
            vm.reportFilter.invoiceName||
            vm.reportFilter.email ||
            vm.reportFilter.position ||
            vm.reportFilter.otherData ||
            vm.reportFilter.accPeopleLastName ||
            vm.reportFilter.accPeopleFirstName ||
            vm.reportFilter.registrationType ||
            vm.reportFilter.workplace ||
            vm.reportFilter.country ||
            vm.reportFilter.payingGroup ||
            vm.reportFilter.optionalServices ||
            vm.reportFilter.congressHotel ||
            vm.reportFilter.presenter ||
            vm.reportFilter.etiquette ||
            vm.reportFilter.closed ||
            vm.reportFilter.onSpot ||
            vm.reportFilter.cancelled;
        }

        function toggleRegIdSelection(regId) {
            // vm.selectedRegIds[regId.toString()] = !isOnlineRegIdSelected(regId);
        }

        function getSelectedRegIds() {
            var regIdList = [];
            for (var prop in vm.selectedRegIds) {
                if (vm.selectedRegIds.hasOwnProperty(prop) && vm.selectedRegIds[prop]) {
                    regIdList.push(parseInt(prop, 10));
                }
            }
            return regIdList;
        }

        function clearRegIdSelection() {
            vm.selectedRegIds = {};
            vm.allRegIdSelected = false;
        }

        function toggleAllRegIdSelection() {
            if (!vm.allRegIdSelected) {
                vm.selectedRegIds = {};
            }
            else {
                vm.selectedRegIds = {};
                for (var i = 0; i < vm.reportList.length; i++) {
                    vm.selectedRegIds[vm.reportList[i].id.toString()] = true;
                }
            }
        }

        function isRegIdSelected(regId) {
            for (var prop in vm.selectedRegIds) {
                if (vm.selectedRegIds.hasOwnProperty(prop) && prop === regId) {
                    return true;
                }
            }
            return false;
        }

        function isAnyRegIdSelected() {
            for (var prop in vm.selectedRegIds) {
                if (vm.selectedRegIds.hasOwnProperty(prop) && vm.selectedRegIds[prop]) {
                    return true;
                }
            }
            return false;
        }


    }
})();
