// STRATEGY PATTERN - Different pricing calculations
public interface PricingStrategy {
    double calculatePrice(double basePrice, int nights);
}

class RegularPricing implements PricingStrategy {
    public double calculatePrice(double basePrice, int nights) {
        return basePrice * nights;
    }
}

class DiscountPricing implements PricingStrategy {
    private double discountPercent;
    public DiscountPricing(double discountPercent) { this.discountPercent = discountPercent; }
    public double calculatePrice(double basePrice, int nights) {
        return (basePrice * nights) * (1 - discountPercent/100);
    }
}

class HolidayPricing implements PricingStrategy {
    public double calculatePrice(double basePrice, int nights) {
        return (basePrice * nights) * 1.2; // 20% extra during holidays
    }
}