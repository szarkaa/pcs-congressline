package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import hu.congressline.pcs.domain.ChargeableItem;
import hu.congressline.pcs.domain.OrderedOptionalService;
import hu.congressline.pcs.domain.PayingGroupItem;
import hu.congressline.pcs.domain.RegistrationRegistrationType;
import hu.congressline.pcs.domain.RoomReservationRegistration;
import hu.congressline.pcs.domain.enumeration.Currency;
import hu.congressline.pcs.service.util.DateInterval;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class DiscountService {

    private final PriceService priceService;

    @SuppressWarnings("MissingJavadocMethod")
    public PayingGroupItem getPayingGroupItemFromRoomReservation(RoomReservationRegistration rrr) {
        for (RoomReservationRegistration registrationItem : rrr.getRoomReservation().getRoomReservationRegistrations()) {
            if (registrationItem.getRegistration().equals(rrr.getRegistration())) {
                return registrationItem.getPayingGroupItem();
            }
        }

        return null;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public BigDecimal getPriceWithDiscount(ChargeableItem chargableItem) {
        PayingGroupItem payingGroupItem;
        BigDecimal priceWithDiscount = null;
        if (chargableItem instanceof RoomReservationRegistration) {
            payingGroupItem = getPayingGroupItemFromRoomReservation((RoomReservationRegistration) chargableItem);
            priceWithDiscount = getRoomReservationPriceWithDiscount(payingGroupItem, (RoomReservationRegistration) chargableItem);
        } else if (chargableItem instanceof RegistrationRegistrationType) {
            payingGroupItem = ((RegistrationRegistrationType) chargableItem).getPayingGroupItem();
            priceWithDiscount = getPriceWithDiscount(payingGroupItem, chargableItem.getChargeableItemPrice(), priceService.getScale(chargableItem));
        } else if (chargableItem instanceof OrderedOptionalService) {
            payingGroupItem = ((OrderedOptionalService) chargableItem).getPayingGroupItem();
            priceWithDiscount = getPriceWithDiscount(payingGroupItem, chargableItem.getChargeableItemPrice(), priceService.getScale(chargableItem));
        }

        return priceWithDiscount;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public BigDecimal getPriceWithDiscount(PayingGroupItem item, @NonNull BigDecimal price, int scale) {
        if (item != null) {
            Integer amountPercentage = item.getAmountPercentage();
            BigDecimal discountPrice;
            if (amountPercentage != null) {
                discountPrice = price.multiply(new BigDecimal(amountPercentage).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
                discountPrice = discountPrice.setScale(scale, RoundingMode.HALF_UP);
            } else {
                discountPrice = new BigDecimal(item.getAmountValue());
                discountPrice = discountPrice.setScale(scale, RoundingMode.HALF_UP);
            }

            return price.subtract(discountPrice);
        }
        return price;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public BigDecimal getRoomReservationPriceWithDiscount(PayingGroupItem item, RoomReservationRegistration rrr) {
        if (item != null && item.getHotelDateFrom() != null && item.getHotelDateTo() != null) {
            DateInterval roomReservationInterval = new DateInterval(rrr.getRoomReservation().getArrivalDate(), rrr.getRoomReservation().getDepartureDate());
            DateInterval itemInterval = new DateInterval(item.getHotelDateFrom(), item.getHotelDateTo());
            final DateInterval intersectInterval = itemInterval.intersect(roomReservationInterval);
            if (intersectInterval != null && intersectInterval.length() > 0) {
                long lengthWithoutDiscount = roomReservationInterval.length() - intersectInterval.length();
                return rrr.getSharedPricePerNight().multiply(new BigDecimal(lengthWithoutDiscount)).setScale(2, RoundingMode.HALF_UP);
            } else {
                return rrr.getSharedPricePerNight();
            }
        } else {
            return getPriceWithDiscount(item, rrr.getChargeableItemPrice(), !Currency.HUF.toString().equalsIgnoreCase(rrr.getChargeableItemCurrency()) ? 2 : 0);
        }
    }

    @SuppressWarnings("MissingJavadocMethod")
    public BigDecimal getOptionalServicePriceWithDiscountPerParticipants(OrderedOptionalService orderedOptionalService) {
        return getPriceWithDiscount(orderedOptionalService).divide(new BigDecimal(orderedOptionalService.getParticipant()), RoundingMode.HALF_UP);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public BigDecimal getAmountOfDiscount(RegistrationRegistrationType rrt) {
        BigDecimal amount = BigDecimal.ZERO;
        if (rrt.getPayingGroupItem() != null) {
            if (rrt.getPayingGroupItem().getAmountValue() != null) {
                return new BigDecimal(rrt.getPayingGroupItem().getAmountValue());
            }
            final BigDecimal priceWithDiscount = getPriceWithDiscount(rrt.getPayingGroupItem(), rrt.getChargeableItemPrice(), priceService.getScale(rrt));
            amount = rrt.getChargeableItemPrice().subtract(priceWithDiscount);
        }
        return amount;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public BigDecimal getAmountOfDiscount(RoomReservationRegistration rrr) {
        BigDecimal amount = BigDecimal.ZERO;
        if (rrr.getPayingGroupItem() != null) {
            amount = rrr.getChargeableItemPrice();
            amount = amount.subtract(getRoomReservationPriceWithDiscount(rrr.getPayingGroupItem(), rrr));
        }
        return amount;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public BigDecimal getAmountOfDiscount(OrderedOptionalService oos) {
        BigDecimal amount = BigDecimal.ZERO;
        if (oos.getPayingGroupItem() != null) {
            final BigDecimal priceWithDiscount = getPriceWithDiscount(oos.getPayingGroupItem(), oos.getChargeableItemPrice(), priceService.getScale(oos));
            amount = oos.getChargeableItemPrice().subtract(priceWithDiscount);
        }
        return amount;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public BigDecimal getSumAmountOfDiscountForRegistration(List<RegistrationRegistrationType> list) {
        BigDecimal sumAmount = BigDecimal.ZERO;
        for (RegistrationRegistrationType registrationRegistrationType : list) {
            sumAmount = sumAmount.add(getAmountOfDiscount(registrationRegistrationType));
        }
        return sumAmount;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public BigDecimal getSumAmountOfDiscountForHotel(List<RoomReservationRegistration> list) {
        BigDecimal sumAmount = BigDecimal.ZERO;
        for (RoomReservationRegistration rrr : list) {
            sumAmount = sumAmount.add(getAmountOfDiscount(rrr));
        }
        return sumAmount;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public BigDecimal getSumAmountOfDiscountForOptionalService(List<OrderedOptionalService> list) {
        BigDecimal sumAmount = BigDecimal.ZERO;
        for (OrderedOptionalService oos : list) {
            sumAmount = sumAmount.add(getAmountOfDiscount(oos));
        }
        return sumAmount;
    }

}
