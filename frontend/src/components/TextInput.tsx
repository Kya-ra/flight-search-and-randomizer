import { useState } from "react";

export default function TextInput() {
  const [value, setValue] = useState<string>("");

  return (
    <div>
      <input
        value={value}
        onChange={e => setValue(e.target.value)}
        placeholder="Type something"
      />
      <p>You typed: {value}</p>
    </div>
  );
}
