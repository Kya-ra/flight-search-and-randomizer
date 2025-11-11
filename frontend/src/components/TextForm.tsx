import { useState } from "react";

export default function TextForm() {
  const [text, setText] = useState<string>("");

  function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault(); // prevent page reload
    alert(`Submitted: ${text}`);
    setText(""); // clear input
  }

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        value={text}
        onChange={e => setText(e.target.value)}
        placeholder="Type here..."
      />
      <button type="submit">Submit</button>
    </form>
  );
}
