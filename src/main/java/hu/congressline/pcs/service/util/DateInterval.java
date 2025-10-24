package hu.congressline.pcs.service.util;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

public class DateInterval {

    private final LocalDate begin;
    private final LocalDate end;

    public DateInterval(LocalDate begin, LocalDate end) {
        if (begin.isAfter(end)) {
            throw new IllegalStateException("The begin of the date interval is later than the end of the date interval.");
        } else {
            this.begin = begin;
            this.end = end;
        }
    }

    public LocalDate getBegin() {
        return this.begin;
    }

    public LocalDate getEnd() {
        return this.end;
    }

    public long getNumberOfDays() {
        return DAYS.between(begin, end);
    }

    public boolean contains(DateInterval interval) {
        return (this.begin.isBefore(interval.getBegin()) || this.begin.equals(interval.getBegin())) && (this.end.isAfter(interval.getEnd()) || this.end.equals(interval.getEnd()));
    }

    public boolean contains(LocalDate d) {
        return !this.begin.isAfter(d) && !this.end.isBefore(d);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public DateInterval intersect(DateInterval interval) {
        if (interval.getEnd().isBefore(this.begin) && interval.getBegin().isAfter(this.end)) {
            return null;
        } else {
            LocalDate b = !interval.getBegin().isBefore(this.begin) && !interval.getBegin().equals(this.begin) ? interval.getBegin() : this.begin;
            LocalDate e = !interval.getEnd().isAfter(this.end) && !interval.getEnd().equals(this.end) ? interval.getEnd() : this.end;
            if (b.isAfter(e)) {
                return null;
            } else {
                return new DateInterval(b, e);
            }
        }
    }

    public long length() {
        return getNumberOfDays();
    }

    @SuppressWarnings("MissingJavadocMethod")
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Begin:").append(this.getBegin()).append(", End:").append(this.getEnd());
        return sb.toString();
    }

    @SuppressWarnings("MissingJavadocMethod")
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            DateInterval that = (DateInterval) o;
            if (this.begin != null) {
                if (!this.begin.equals(that.begin)) {
                    return false;
                }
            } else if (that.begin != null) {
                return false;
            }

            if (this.end != null) {
                return this.end.equals(that.end);
            } else {
                return that.end == null;
            }
        } else {
            return false;
        }
    }

    @SuppressWarnings("MissingJavadocMethod")
    public int hashCode() {
        int result = this.begin != null ? this.begin.hashCode() : 0;
        result = 31 * result + (this.end != null ? this.end.hashCode() : 0);
        return result;
    }
}
