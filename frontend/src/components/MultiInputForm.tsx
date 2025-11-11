import { useState } from "react";
import { registerLocale } from "react-datepicker";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { enIE } from "date-fns/locale/en-IE";

registerLocale("en-IE", enIE);

export default function SearchForm() {
  const [form, setForm] = useState({
    origin: "",
    destination: "",
    outbound: null as Date | null,
    return: null as Date | null,
  });

  function handleChange(e: React.ChangeEvent<HTMLInputElement>) {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  }

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    console.log("Form submitted:", form);

    const flight_type = form.return ? 2 : 1;

    // Convert dates to YYYY-MM-DD strings
    const outboundDateStr = form.outbound
      ? form.outbound.toISOString().split("T")[0]
      : "";
    const returnDateStr = form.return
      ? form.return.toISOString().split("T")[0]
      : undefined;

    // Build query string
    const params = new URLSearchParams({
      origin: form.origin,
      destination: form.destination,
      outboundDate: outboundDateStr,
      flight_type: flight_type.toString(),
    });

    if (returnDateStr) {
      params.append("returnDate", returnDateStr);
    }

    try {
      const response = await fetch(`http://localhost:8080/api/flights/search?${params.toString()}`);
      if (!response.ok)
        throw new Error(`HTTP error! status: ${response.status}`);
      const data = await response.json();
      console.log("Flight search response:", data); // you can replace with popup/UI later
    } catch (err) {
      console.error("Error fetching flights:", err);
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <input
        name="origin"
        value={form.origin}
        onChange={handleChange}
        placeholder="Origin"
        required
      />

      <input
        name="destination"
        value={form.destination}
        onChange={handleChange}
        placeholder="Destination"
        required
      />

      <DatePicker
        selected={form.outbound}
        onChange={(date) => setForm((prev) => ({ ...prev, outbound: date }))}
        placeholderText="Outbound"
        dateFormat="dd/MM/yyyy"
        locale="en-IE"
        required
      />

      <DatePicker
        selected={form.return}
        onChange={(date) => setForm((prev) => ({ ...prev, return: date }))}
        placeholderText="Return"
        dateFormat="dd/MM/yyyy"
        locale="en-IE"
      />

      <button type="submit">Search</button>
    </form>
  );
}