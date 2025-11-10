import { useState } from "react";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

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
        required
      />

      <DatePicker
        selected={form.return}
        onChange={(date) =>
          setForm((prev) => ({ ...prev, return: date }))
        }
        placeholderText="Return"
        dateFormat="dd/MM/yyyy"
      />

      <button type="submit">Search</button>
    </form>
  );
}