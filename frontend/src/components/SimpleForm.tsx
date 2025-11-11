import { useState } from "react";

export default function SimpleForm() {
  const [value, setValue] = useState<string>("");

  function handleChange(e: React.ChangeEvent<HTMLInputElement>) {
    setValue(e.target.value);
  }

  return (
    <input value={value} onChange={handleChange} placeholder="Type something" />
  );
}