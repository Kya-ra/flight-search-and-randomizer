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
    return: null as Date | null
  });

  function handleChange(
    e: React.ChangeEvent<HTMLInputElement>
  ) {
    const { name, value } = e.target;

    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  }

  function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    console.log("Form submitted:", form);
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
        onChange={(date) =>
          setForm((prev) => ({ ...prev, outbound: date }))
        }
        placeholderText="Outbound"
        dateFormat="dd/MM/yyyy"
        locale="en-IE"
        required
      />

      <DatePicker
        selected={form.return}
        onChange={(date) =>
          setForm((prev) => ({ ...prev, return: date }))
        }
        placeholderText="Return"
        dateFormat="dd/MM/yyyy"
        locale="en-IE"
      />

      <button type="submit">Search</button>
    </form>
  );
}