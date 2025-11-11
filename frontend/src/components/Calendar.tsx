import { useState } from "react";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

export default function CalendarPicker() {
  const [date, setDate] = useState<Date | null>(null);

  return (
    <div>
      <DatePicker
        selected={date}
        onChange={(newDate) => setDate(newDate)}
      />
    </div>
  );
}