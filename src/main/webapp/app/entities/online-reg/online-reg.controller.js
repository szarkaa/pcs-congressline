(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('OnlineRegController', OnlineRegController);

    OnlineRegController.$inject = ['$timeout', '$filter', '$scope', '$state', '$stateParams', '$translate', 'tmhDynamicLocale', 'congress', 'registration', 'OnlineReg', 'DataUtils'];

    function OnlineRegController ($timeout, $filter, $scope, $state, $stateParams, $translate, tmhDynamicLocale, congress, registration, OnlineReg, DataUtils) {
        var vm = this;
        vm.isSending = false;
        vm.isSubmitSuccess = false;
        vm.selectedRoomOutOfStock = false;
        vm.originalLanguage = $translate.use();
        vm.language = $stateParams.language;
        vm.currency = $stateParams.currency;
        vm.isLangHu = 'hu' === vm.language;
        vm.registration = registration;
        vm.congress = congress;
        vm.nights = '';
        vm.config = congress.onlineRegConfig;
        vm.registration.invoiceCountry = vm.config.defaultCountry;
        vm.invoiceInfoCopied = false;

        vm.discountCodeText = null;
        vm.discountCode = null;
        vm.fileUploadDisplay = null;

        initDefaultCountry();

        $translate.use(vm.language);
        tmhDynamicLocale.set(vm.language);

        vm.headerStyle = headerStyle;
        vm.setFile = setFile;
        vm.clearFile = clearFile;
        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;
        vm.submit = submit;
        vm.clear = clear;
        vm.getCurrency = getCurrency;
        vm.getHotelRooms = getHotelRooms;
        vm.getArrivalDates = getArrivalDates;
        vm.getDepartureDates = getDepartureDates;
        vm.calculateNights = calculateNights;
        vm.openRoomExternalLink = openRoomExternalLink;
        vm.calculateOSPrice = calculateOSPrice;
        vm.calculateRTTotal = calculateRTTotal;
        vm.calculateHRTotal = calculateHRTotal;
        vm.calculateOSTotal = calculateOSTotal;
        vm.displayRTSubTotal = displayRTSubTotal;
        vm.displayHRSubTotal = displayHRSubTotal;
        vm.displayOSSubTotal = displayOSSubTotal;
        vm.displayGrandTotal = displayGrandTotal;
        vm.displayRTSubTotalDiscount = displayRTSubTotalDiscount;
        vm.displayHRSubTotalDiscount = displayHRSubTotalDiscount;
        vm.displayOSSubTotalDiscount = displayOSSubTotalDiscount;
        vm.displayGrandTotalDiscount = displayGrandTotalDiscount;
        vm.displayRTTotal = displayRTTotal;
        vm.displayHRTotal = displayHRTotal;
        vm.displayOSTotal = displayOSTotal;
        vm.displayGrandTotalWithDiscount = displayGrandTotalWithDiscount;

        vm.isCardDataRequired = isCardDataRequired;
        vm.isCheckDataRequired = isCheckDataRequired;
        vm.isHotelRoomDatesRequired = isHotelRoomDatesRequired;
        vm.resetPaymentMethodState = resetPaymentMethodState;
        vm.isRegTypeSelected = isRegTypeSelected;
        vm.hasAnyAvailableRoom = hasAnyAvailableRoom;
        vm.addAccPeople = addAccPeople;
        vm.removeAccPeople = removeAccPeople;
        vm.selectExtraRegType = selectExtraRegType;
        vm.copyInvoiceInfo = copyInvoiceInfo;
        vm.setRegTypeRadioButtonValue = setRegTypeRadioButtonValue;
        vm.isRegTypeRadioButtonChecked = isRegTypeRadioButtonChecked;
        vm.setRoomRadioButtonValue = setRoomRadioButtonValue;
        vm.isRoomRadioButtonChecked = isRoomRadioButtonChecked;
        vm.hasExtraSecondRegFeeValue = hasExtraSecondRegFeeValue;
        vm.openCongressWebsite = openCongressWebsite;
        vm.isEmailIdentical = isEmailIdentical;
        vm.hasCustom1DataSelectableValues = hasCustom1DataSelectableValues;
        vm.hasCustom2DataSelectableValues = hasCustom2DataSelectableValues;
        vm.getCustom1DataSelectableValues = getCustom1DataSelectableValues;
        vm.getCustom2DataSelectableValues = getCustom2DataSelectableValues;
        vm.hasOtherDataSelectableValues = hasOtherDataSelectableValues;
        vm.getOtherDataSelectableValues = getOtherDataSelectableValues;
        vm.hasPositionSelectableValues = hasPositionSelectableValues;
        vm.getPositionSelectableValues = getPositionSelectableValues;
        vm.hasWorkplaceSelectableValues = hasWorkplaceSelectableValues;
        vm.getWorkplaceSelectableValues = getWorkplaceSelectableValues;
        vm.getTitleSelectableValues = getTitleSelectableValues;
        vm.checkDiscountCode = checkDiscountCode;
        vm.getValueByLanguage = getValueByLanguage;
        vm.getCustomerInformationFilename = getCustomerInformationFilename;
        vm.getPrivacyPolicyFilename = getPrivacyPolicyFilename;


        OnlineReg.queryCountries(function (result) {
            vm.countries = result;
        });

        OnlineReg.queryRegistrationTypes({uuid: $stateParams.uuid, currency: $stateParams.currency}, function(result) {
            vm.registrationTypes = result;
        });

        OnlineReg.queryHotelRooms({uuid: $stateParams.uuid, currency: $stateParams.currency}, function(result) {
            vm.hotelRooms = getHotelRooms(result);
        });

        OnlineReg.queryOptionalServices({uuid: $stateParams.uuid, currency: $stateParams.currency}, function(result) {
            vm.optionalServices = result;
        });

        OnlineReg.queryCustomQuestions({uuid: $stateParams.uuid, currency: $stateParams.currency}, function (result) {
            vm.customQuestions = result;
        });

        function buildOnlineReg() {
            var reg = { // in clear and state resolve function as well!!!
                attachmentName: vm.registration.attachment.name,
                attachmentContentType: vm.registration.attachment.fileContentType,
                attachmentFile: vm.registration.attachment.file,
                title: vm.registration.title,
                lastName: vm.registration.lastName,
                firstName: vm.registration.firstName,
                position: vm.registration.position,
                department: vm.registration.department,
                workplace: vm.registration.workplace,
                zipCode: vm.registration.zipCode,
                city: vm.registration.city,
                countryId: vm.registration.country ? vm.registration.country.id : null,
                street: vm.registration.street,
                phone: vm.registration.phone,
                email: vm.registration.email,
                otherData: vm.registration.otherData,
                custom1Data: vm.registration.custom1Data,
                custom2Data: vm.registration.custom2Data,
                roomId: vm.registration.room ? vm.registration.room : null,
                arrivalDate: vm.registration.arrivalDate,
                departureDate: vm.registration.departureDate,
                roommate: vm.registration.roommate,
                roomRemark: vm.registration.roomRemark,
                registrationTypeId: vm.registration.regType ? vm.registration.regType : null,
                extraRegTypes: [],
                optionalServices: [],
                customAnswers: [],
                paymentMethod: vm.registration.paymentMethod,
                cardType: vm.registration.cardType,
                checkName: vm.registration.checkName,
                checkAddress: vm.registration.checkAddress,
                cardHolderName: vm.registration.cardHolderName,
                cardHolderAddress: vm.registration.cardHolderAddress,
                cardNumber: vm.registration.cardNumber,
                cardExpiryMonth: vm.registration.cardExpiryMonth,
                cardExpiryYear: vm.registration.cardExpiryYear,
                invoiceName: vm.registration.invoiceName,
                invoiceCountryId: vm.registration.invoiceCountry ? vm.registration.invoiceCountry.id : null,
                invoiceZipCode: vm.registration.invoiceZipCode,
                invoiceCity: vm.registration.invoiceCity,
                invoiceAddress: vm.registration.invoiceAddress,
                invoiceReferenceNumber:vm.registration.invoiceReferenceNumber,
                invoiceTaxNumber: vm.registration.invoiceTaxNumber,
                termsAndConditions: vm.registration.termsAndConditions,
                discountCode: vm.registration.discountCode,
                discountPercentage: vm.registration.discountPercentage,
                discountType: vm.registration.discountType,
                gdpr: vm.registration.gdpr,
                newsletter: vm.registration.newsletter,
                currency: vm.currency,
                uuid: vm.registration.uuid
            };

            var erts = vm.registration.extraRegTypes;
            for (var prop in erts) {
                if (erts.hasOwnProperty(prop) && erts[prop]) {
                    var regTypeVM = { registrationTypeId: prop, accompanies: []};

                    for (var j = 0; j < erts[prop].length; j++) {
                        regTypeVM.accompanies.push(erts[prop][j]);
                    }
                    reg.extraRegTypes.push(regTypeVM);
                }
            }

            var oss = vm.registration.optionalServices;
            for (var prop in oss) {
                if (oss.hasOwnProperty(prop) && oss[prop]) {
                    var optionalServiceVM = { optionalServiceId: prop, participants: oss[prop]};
                    reg.optionalServices.push(optionalServiceVM);
                }
            }

            var cq = vm.registration.customAnswers;
            for (var prop in cq) {
                if (cq.hasOwnProperty(prop) && cq[prop]) {
                    var customAnswerVM = { question: { id: prop }, answer: cq[prop]};
                    reg.customAnswers.push(customAnswerVM);
                }
            }
            return reg;
        }

        function isRoomAvailable(room, arrivalDate, departureDate) {
            if (!room || !departureDate || !arrivalDate) {
                return false;
            }

            var deptDate = new Date(departureDate);
            deptDate.setHours(0, 0, 0, 0);

            for (var i = 0; room && room.reservations && i < room.reservations.length; i++) {
                var reservation = room.reservations[i];
                var reservationDate = new Date(reservation.reservationDate);
                reservationDate.setHours(0, 0, 0, 0);
                //iterate over selected arrival and departure dates
                var arrDate = new Date(arrivalDate);
                arrDate.setHours(0, 0, 0, 0);
                for (; arrDate < deptDate; arrDate.setDate(arrDate.getDate() + 1)) {
                    if (reservationDate.getTime() === arrDate.getTime() && reservation.reserved >= room.quantity) {
                        return false;
                    }
                }
            }
            return true;
        }

        function hasAnyAvailableRoom() {
            if (!vm.hotelRooms) {
                return false;
            }
            for (var i = 0; i < vm.hotelRooms.length; i++) {
                if (vm.hotelRooms[i].rooms.length) {
                    return true;
                }
            }
            return false;
        }

        function submit() {
            vm.isSending = true;
            //We have to check the actual available number of the selected room at the moment of saving the online
            OnlineReg.queryHotelRooms({uuid: $stateParams.uuid, currency: $stateParams.currency}, function(result) {
                vm.selectedRoomOutOfStock = false;
                if (vm.registration.room) {
                    var hotelRooms = getHotelRooms(result);
                    var room = getRoomById(hotelRooms, vm.registration.room);
                    if (!isRoomAvailable(room, vm.registration.arrivalDate, vm.registration.departureDate)) {
                        vm.registration.room = null;
                        vm.registration.arrivalDate = null;
                        vm.registration.departureDate = null;
                        vm.nights = '';
                        vm.isSending = false;
                        vm.selectedRoomOutOfStock = true;
                        vm.hotelRooms = hotelRooms;
                    }
                }

                if (!vm.selectedRoomOutOfStock) {
                    OnlineReg.save(buildOnlineReg(), onSaveSuccess, onSaveError);
                }
            });
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:onlineRegSubmitted', result);
            var grandTotal = calculateRTSubTotal() + calculateHRSubTotal() + calculateOSSubTotal();
            if (vm.config.paymentSupplier === 'STRIPE' && grandTotal > 0) {
                try {
                    var stripe = Stripe(vm.config.stripePublicKey);
                    stripe.redirectToCheckout({sessionId: result.sessionId})
                        .then(function (result) {
                            // If redirection fails, display an error to the customer.
                            if (result.error) {
                                var displayError = document.getElementById('error-message');
                                displayError.textContent = result.error.message;
                            }
                        });
                }
                catch (e) {
                    alert('Unexpected error during the online payment process, please contact the congress organizer!');
                    console.error(e);
                }
            }
            else {
                vm.isSubmitSuccess = true;
            }
        }

        function onSaveError () {
            vm.isSending = false;
        }

        function getHotelRooms(hotelRooms) {
            var hotels = [];
            for (var i = 0; i < hotelRooms.length; i++) {
                var roomArrayLength = Math.max(hotelRooms[i].singleList.length, hotelRooms[i].doubleList.length);
                var hotel = {
                    name: hotelRooms[i].name,
                    zipCode: hotelRooms[i].zipCode,
                    city: hotelRooms[i].city,
                    street: hotelRooms[i].street,
                    rowSpan: roomArrayLength,
                    rooms: []
                };

                for (var j = 0; j < roomArrayLength; j++) {
                    var room = {};
                    if (hotelRooms[i].singleList[j]) {
                        room.single = hotelRooms[i].singleList[j];
                    }

                    if (hotelRooms[i].doubleList[j]) {
                        room.double = hotelRooms[i].doubleList[j];
                    }
                    hotel.rooms.push(room);
                }
                hotels.push(hotel)
            }
            return hotels;
        }

        function getRegTypeById(id) {
            if (!vm.registrationTypes) return null;
            for (var i = 0; i < vm.registrationTypes.length; i++) {
                if (vm.registrationTypes[i].id == id) {
                    return vm.registrationTypes[i];
                }
            }
            return null;
        }

        function getRoomById(hotelRooms, id) {
            if (!hotelRooms) return null;
            for (var i = 0; i < hotelRooms.length; i++) {
                var hotel = hotelRooms[i];
                for (var j = 0; j < hotel.rooms.length; j++) {

                    if (hotel.rooms[j].single && hotel.rooms[j].single.id == id) {
                        return hotel.rooms[j].single;
                    }

                    if (hotel.rooms[j].double && hotel.rooms[j].double.id == id) {
                        return hotel.rooms[j].double;
                    }
                }
            }
            return null;
        }

        function getOptionalServiceById(id) {
            if (!vm.optionalServices) return null;
            for (var i = 0; i < vm.optionalServices.length; i++) {
                if (vm.optionalServices[i].id == id) {
                    return vm.optionalServices[i];
                }
            }
            return null;
        }

        function getCurrency() {
            if (vm.registrationTypes && vm.registrationTypes.length) {
                return vm.registrationTypes[0].currency;
            }
            else if (vm.optionalServices && vm.optionalServices.length) {
                return vm.optionalServices[0].currency;
            }
            else if (vm.hotelRooms && vm.hotelRooms.length) {
                if (vm.hotelRooms[0].rooms[0].single) {
                    return vm.hotelRooms[0].rooms[0].single.currency;
                }
                if (vm.hotelRooms[0].rooms[0].double) {
                    return vm.hotelRooms[0].rooms[0].double.currency;
                }
            }
            return '';
        }

        function getArrivalDates() {
            if (!vm.arrivalDates) {
                vm.arrivalDates = [];
                vm.arrivalDates.push({value: null, label: $filter('translate')('pcsApp.onlineReg.form.please.select')});
                var d = new Date(vm.congress.startDate);
                var diffTime = Math.abs(vm.congress.endDate.getTime() - vm.congress.startDate.getTime());
                var diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

                for (var i = -4; i < diffDays; i++) {
                    var date = (new Date(vm.congress.startDate)).setDate((d).getDate() + i);
                    vm.arrivalDates.push({value: date, label: $filter('date')(date, 'longDate')});
                }
            }
            return vm.arrivalDates;
        }

        function getDepartureDates() {
            if (!vm.departureDates) {
                vm.departureDates = [];
                vm.departureDates.push({value: null, label: $filter('translate')('pcsApp.onlineReg.form.please.select')});
                var diffTime = Math.abs(vm.congress.endDate.getTime() - vm.congress.startDate.getTime());
                var diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
                var d = new Date(vm.congress.startDate);
                for (var i = 1; i < diffDays + 4; i++) {
                    var date = (new Date(vm.congress.startDate)).setDate((d).getDate() + i);
                    vm.departureDates.push({value: date, label: $filter('date')(date, 'longDate')});
                }
            }
            return vm.departureDates;
        }

        function calculateNights() {
            var diffTime = vm.registration.departureDate && vm.registration.arrivalDate ? vm.registration.departureDate - vm.registration.arrivalDate : 0;
            var diffDays = diffTime ? Math.ceil(diffTime / (1000 * 60 * 60 * 24)) : '';
            vm.nights = diffDays;
        }

        function calculateOSPrice(os) {
            if (vm.registration.optionalServices[os.id + '']) {
                return (os.price * vm.registration.optionalServices[os.id + '']) + ' ' + os.currency;
            }
            return '';
        }

        function calculateNormalRTSubTotal() {
            var rt = getRegTypeById(vm.registration.regType);
            return rt ? rt.currentRegFee : 0;
        }

        function calculateExtraRTSubTotal() {
            var subTotal = 0;
            var selectedRegType = vm.registration.extraRegTypes;
            for (var prop in selectedRegType) {
                if (selectedRegType.hasOwnProperty(prop) && selectedRegType[prop]) {
                    var rt = getRegTypeById(prop);
                    subTotal += rt.currentRegFee * Math.max(1, selectedRegType[prop].length);
                }
            }
            return subTotal;
        }

        function calculateRTSubTotal() {
            return calculateNormalRTSubTotal() + calculateExtraRTSubTotal();
        }

        function calculateRTSubTotalDiscount() {
            if (vm.registration.discountCode && vm.registration.discountPercentage &&
                (!vm.registration.discountType || vm.registration.discountType === 'REGISTRATION' || vm.registration.discountType === 'NORMAL_REGISTRATION' || vm.registration.discountType === 'EXTRA_REGISTRATION')) {
                var normalSubTotal = calculateNormalRTSubTotal();
                var extraSubTotal = calculateExtraRTSubTotal();
                if (vm.registration.discountType === 'NORMAL_REGISTRATION') {
                    return Math.round(normalSubTotal * (vm.registration.discountPercentage / 100));
                } else if (vm.registration.discountType === 'EXTRA_REGISTRATION') {
                    return Math.round(extraSubTotal * (vm.registration.discountPercentage / 100));
                } else {
                    return Math.round((normalSubTotal + extraSubTotal) * (vm.registration.discountPercentage / 100));
                }
            }
            return 0;
        }

        function calculateRTTotal() {
            return calculateRTSubTotal() - calculateRTSubTotalDiscount();
        }

        function calculateHRSubTotal() {
            var room = vm.registration.room ? getRoomById(vm.hotelRooms, vm.registration.room) : null;
            return vm.nights && room ? room.price * vm.nights : 0;
        }

        function calculateHRSubTotalDiscount() {
            if (vm.registration.discountCode && vm.registration.discountPercentage &&
                (!vm.registration.discountType || vm.registration.discountType === 'HOTEL')) {
                var subTotal = calculateHRSubTotal();
                return Math.round(subTotal * (vm.registration.discountPercentage / 100));
            }
            return 0;
        }

        function calculateHRTotal() {
            return calculateHRSubTotal() - calculateHRSubTotalDiscount();
        }

        function calculateOSSubTotal() {
            var subTotal = 0;
            var selectedOptionalService = vm.registration.optionalServices;
            for (var prop in selectedOptionalService) {
                if (selectedOptionalService.hasOwnProperty(prop) && selectedOptionalService[prop]) {
                    var os = getOptionalServiceById(prop);
                    subTotal += os.price * selectedOptionalService[prop];
                }
            }
            return subTotal;
        }

        function calculateOSSubTotalDiscount() {
            if (vm.registration.discountCode && vm.registration.discountPercentage &&
                (!vm.registration.discountType || vm.registration.discountType === 'OPTIONAL_SERVICE')) {
                var subTotal = calculateOSSubTotal();
                return Math.round(subTotal * (vm.registration.discountPercentage / 100));
            }
            return 0;
        }

        function calculateOSTotal() {
            return calculateOSSubTotal() - calculateOSSubTotalDiscount();
        }

        function displayRTSubTotal() {
            var subTotal = calculateRTSubTotal();
            return subTotal ? $filter('number')(subTotal) + ' ' + getCurrency() : '';
        }

        function displayRTSubTotalDiscount() {
            var discount = calculateRTSubTotalDiscount();
            return discount ? $filter('number')(discount) + ' ' + getCurrency() : '';
        }

        function displayRTTotal() {
            var total = calculateRTSubTotal() - calculateRTSubTotalDiscount();
            return total ? $filter('number')(total) + ' ' + getCurrency() : '';
        }

        function displayHRSubTotal() {
            var subTotal = calculateHRSubTotal();
            return subTotal ? $filter('number')(subTotal) + ' ' + getCurrency() : '';
        }

        function displayHRSubTotalDiscount() {
            var discount = calculateHRSubTotalDiscount();
            return discount ? $filter('number')(discount) + ' ' + getCurrency() : '';
        }

        function displayHRTotal() {
            var total = calculateHRSubTotal() - calculateHRSubTotalDiscount();
            return total ? $filter('number')(total) + ' ' + getCurrency() : '';
        }

        function displayOSSubTotal() {
            var subTotal = calculateOSSubTotal();
            return subTotal ? $filter('number')(subTotal) + ' ' + getCurrency() : '';
        }

        function displayOSSubTotalDiscount() {
            var discount = calculateOSSubTotalDiscount();
            return discount ? $filter('number')(discount) + ' ' + getCurrency() : '';
        }

        function displayOSTotal() {
            var total = calculateOSSubTotal() - calculateOSSubTotalDiscount();
            return total ? $filter('number')(total) + ' ' + getCurrency() : '';
        }

        function displayGrandTotal() {
            var grandTotal = calculateRTSubTotal() + calculateHRSubTotal() + calculateOSSubTotal();
            return grandTotal ? $filter('number')(grandTotal) + ' ' + getCurrency() : '';
        }

        function displayGrandTotalDiscount() {
            var discount = calculateRTSubTotalDiscount() + calculateHRSubTotalDiscount() + calculateOSSubTotalDiscount();
            return discount ? $filter('number')(discount) + ' ' + getCurrency() : '';
        }

        function displayGrandTotalWithDiscount() {
            var grandTotal = calculateRTSubTotal() + calculateHRSubTotal() + calculateOSSubTotal();
            var grandTotalDiscount = calculateRTSubTotalDiscount() + calculateHRSubTotalDiscount() + calculateOSSubTotalDiscount();
            return (grandTotal ? $filter('number')(grandTotal - grandTotalDiscount) : '0') + ' ' + getCurrency();
        }

        function isCardDataRequired() {
            return vm.registration.cardType == 'AMEX';
        }

        function isCheckDataRequired() {
            return vm.registration.paymentMethod == 'CHECK';
        }

        function isHotelRoomDatesRequired() {
            return vm.registration.room ? true : false;
        }

        function resetPaymentMethodState() {
            if (vm.registration.paymentMethod != 'CARD') {
                vm.registration.cardType = null;
                vm.registration.cardHolderName = null;
                vm.registration.cardHolderAddress = null;
                vm.registration.cardExpiryMonth = null;
                vm.registration.cardExpiryYear = null;
            }
        }

        function openRoomExternalLink(room) {
            window.open(room.onlineExternalLink, '_blank');
        }

        function setFile($file, pcsFile) {
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        pcsFile.name = $file.name;
                        pcsFile.file = base64Data;
                        pcsFile.fileContentType = $file.type ? $file.type : 'application/octet-stream';
                    });
                });
            }
        }

        function clearFile(file) {
            file.name = null;
            file.fileContentType = null;
            file.file = null;
        }

        function clear() { // in state resolve as well!!!
            vm.registration.attachment = {file: null, fileContentType: null, name: null};
            vm.registration.title = null;
            vm.registration.lastName = null;
            vm.registration.firstName = null;
            vm.registration.position = null;
            vm.registration.department = null;
            vm.registration.workplace = null;
            vm.registration.zipCode = null;
            vm.registration.city = null;
            vm.registration.country = null;
            vm.registration.street = null;
            vm.registration.phone = null;
            vm.registration.email = null;
            vm.registration.email2 = null;
            vm.registration.otherData = null;
            vm.registration.custom1Data = null;
            vm.registration.custom2Data = null;
            vm.registration.room = null;
            vm.registration.arrivalDate = null;
            vm.registration.departureDate = null;
            vm.registration.roommate = null;
            vm.registration.roomRemark = null;
            vm.registration.regType = null;
            vm.registration.extraRegTypes = {};
            vm.registration.optionalServices = {};
            vm.registration.customQuestions = {};
            vm.registration.paymentMethod = null;
            vm.registration.cardType = null;
            vm.registration.checkName = null;
            vm.registration.checkAddress = null;
            vm.registration.cardHolderName= null;
            vm.registration.cardHolderAddress= null;
            vm.registration.cardNumber= null;
            vm.registration.cardExpiryMonth= null;
            vm.registration.cardExpiryYear= null;
            vm.registration.invoiceName = null;
            vm.registration.invoiceCountry = null;
            vm.registration.invoiceZipCode = null;
            vm.registration.invoiceCity = null;
            vm.registration.invoiceAddress = null;
            vm.registration.invoiceReferenceNumber = null;
            vm.registration.invoiceTaxNumber = null;
            vm.registration.termsAndConditions = null;
            vm.registration.discountCode = null;
            vm.registration.discountPercentage = null;
            vm.registration.discountType = null;
            vm.registration.gdpr = null;
            vm.registration.newsletter = null;

            vm.nights = '';
        }

        function headerStyle() {
            return vm.config.colorCode ? {'color': '#ffffff', 'background-color': vm.config.colorCode } : {};
        }

        function isRegTypeSelected(rt) {
            if (rt.registrationType != 'ACCOMPANYING_FEE') {
                return false;
            }
            var selectedRegType = vm.registration.extraRegTypes;
            for (var prop in selectedRegType) {
                if (selectedRegType.hasOwnProperty(prop) && prop == rt.id && selectedRegType[prop] && selectedRegType[prop].length) {
                    return true;
                }
            }
            return false;
        }

        function selectExtraRegType(rt) {
            if (vm.registration.extraRegTypes[rt.id + ''] != null && rt.registrationType == 'ACCOMPANYING_FEE') {
                addAccPeople(rt.id);
            }
        }

        function addAccPeople(id) {
            vm.registration.extraRegTypes[id + ''].push({lastName: null, firstName: null});
        }

        function removeAccPeople(id, index) {
            vm.registration.extraRegTypes[id + ''].splice(index, 1);
        }

        function copyInvoiceInfo() {
            if (vm.invoiceInfoCopied) {
                vm.registration.invoiceCountry = vm.registration.country || vm.congress.defaultCountry;
                vm.registration.invoiceZipCode = vm.registration.zipCode || '';
                vm.registration.invoiceCity = vm.registration.city || '';
                vm.registration.invoiceAddress = vm.registration.street || '';
            }
            else {
                vm.registration.invoiceName = '';
                vm.registration.invoiceCountry = vm.congress.defaultCountry;
                vm.registration.invoiceZipCode = '';
                vm.registration.invoiceCity = '';
                vm.registration.invoiceAddress = '';
            }
        }

        function setRegTypeRadioButtonValue (event) {
            if (vm.registration.regType == event.target.value) {
                vm.registration.regType = null;

            }
            else {
                vm.registration.regType = event.target.value;
            }
        }

        function isRegTypeRadioButtonChecked(id) {
            return vm.registration.regType == id;
        }

        function setRoomRadioButtonValue (event) {
            if (vm.registration.room == event.target.value) {
                vm.registration.room = null;

            }
            else {
                vm.registration.room = event.target.value;
            }
        }

        function isRoomRadioButtonChecked(id) {
            return vm.registration.room == id;
        }

        function hasExtraSecondRegFeeValue() {
            for (var i = 0; vm.registrationTypes && i < vm.registrationTypes.length; i++) {
               if (vm.registrationTypes[i].onlineType === 'EXTRA' && vm.registrationTypes[i].secondRegFee) {
                   return true;
               }
            }
            return false;
        }

        function openCongressWebsite() {
            window.open(vm.congress.website.startsWith('http://') ? vm.congress.website : 'http://' + vm.congress.website, '_blank');
        }

        function isEmailIdentical () {
            if (!vm.registration.email || !vm.registration.email2) {
                return true;
            }
            else {
                return vm.registration.email === vm.registration.email2;
            }
        }

        function hasSelectableValuesByLanguage(fieldName) {
            let text;
            switch(vm.language) {
                case "hu":
                    text = 'Hu';
                    break;
                case "en":
                    text = 'En';
                    break;
                case "es":
                    text = 'Es';
                    break;
                case "pt":
                    text = 'Pt';
                    break;
                default:
                    text = 'Hu';
            }
            return vm.config[fieldName + text + 'Values'] && vm.config[fieldName + text + 'Values'].length;
        }

        function getSelectableValuesByLanguage(fieldName) {
            let text;
            switch(vm.language) {
                case "hu":
                    text = 'Hu';
                    break;
                case "en":
                    text = 'En';
                    break;
                case "es":
                    text = 'Es';
                    break;
                case "pt":
                    text = 'Pt';
                    break;
                default:
                    text = 'Hu';
            }
            return vm.config[fieldName + text + 'Values'];
        }

        function hasCustom1DataSelectableValues() {
            return hasSelectableValuesByLanguage('custom1Data');
        }

        function getCustom1DataSelectableValues() {
            return getSelectableValuesByLanguage('custom1Data');
        }

        function hasCustom2DataSelectableValues() {
            return hasSelectableValuesByLanguage('custom2Data');
        }

        function getCustom2DataSelectableValues() {
            return getSelectableValuesByLanguage('custom2Data');
        }

        function hasOtherDataSelectableValues() {
            return hasSelectableValuesByLanguage('otherData');
        }

        function getOtherDataSelectableValues() {
            return getSelectableValuesByLanguage('otherData');
        }

        function hasPositionSelectableValues() {
            return hasSelectableValuesByLanguage('position');
        }

        function getPositionSelectableValues() {
            return getSelectableValuesByLanguage('position');
        }

        function hasWorkplaceSelectableValues() {
            return hasSelectableValuesByLanguage('workplace');
        }

        function getWorkplaceSelectableValues() {
            return getSelectableValuesByLanguage('workplace');
        }

        function getTitleSelectableValues() {
            return vm.isLangHu ? ['Dr.', 'Prof.'] : ['Dr.', 'Ms.', 'Mr.', 'Mrs.', 'Prof.'];
        }

        function initDefaultCountry() {
            if (vm.config.defaultCountry) {
                vm.registration.country = vm.config.defaultCountry;
            }
        }

        function checkDiscountCode() {
            if (vm.discountCodeText && vm.discountCodeText.length >= 5) {
                OnlineReg.getDiscountCode({uuid: vm.congress.uuid, code: vm.discountCodeText}).$promise.then(function (result) {
                    vm.registration.discountCode = result.code;
                    vm.registration.discountPercentage = result.discountPercentage;
                    vm.registration.discountType = result.discountType;
                }, function (error) {
                    vm.registration.discountCode = null;
                    vm.registration.discountPercentage = null;
                    vm.registration.discountType = null;
                });
            }
        }

        function getValueByLanguage(fieldName) {
            let text;
            switch(vm.language) {
                case "hu":
                    text = vm.config[fieldName + 'Hu'];
                    break;
                case "en":
                    text = vm.config[fieldName + 'En'];
                    break;
                case "es":
                    text = vm.config[fieldName + 'Es'];
                    break;
                case "pt":
                    text = vm.config[fieldName + 'Pt'];
                    break;
                default:
                    text = vm.config[fieldName + 'Hu'];
            }
            return text;
        }

        function getCustomerInformationFilename() {
            let text;
            switch(vm.language) {
                case "hu":
                    text = 'congressline_vasarloi_tajekoztato.pdf';
                    break;
                case "en":
                    text = 'congressline_customer_information.pdf'
                    break;
                case "es":
                    text = 'congressline_customer_information.pdf'
                    break;
                case "pt":
                    text = 'congressline_customer_information.pdf'
                    break;
                default:
                    text = 'congressline_vasarloi_tajekoztato.pdf';
            }
            return text;
        }

        function getPrivacyPolicyFilename() {
            let text;
            switch(vm.language) {
                case "hu":
                    text = 'adatkezelesi-tajekoztato/';
                    break;
                case "en":
                    text = 'en/privacy-policy/';
                    break;
                case "es":
                    text = 'en/privacy-policy/';
                    break;
                case "pt":
                    text = 'en/privacy-policy/';
                    break;
                default:
                    text = 'adatkezelesi-tajekoztato/';
            }
            return text;
        }
    }
})();
