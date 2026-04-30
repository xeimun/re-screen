import React from "react";

const SearchBar = ({query, setQuery, onSearch}) => {
    const handleKeyDown = (e) => {
        if (e.key === "Enter" && onSearch) {
            onSearch();
        }
    };

    return (
        <div className="app-surface flex w-full max-w-md items-center rounded-full px-4 py-2">
            <input
                type="text"
                placeholder="예) 이웃집 토토로"
                className="flex-grow bg-transparent text-[#17304f] outline-none placeholder:text-[#6e7f97]"
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                onKeyDown={handleKeyDown}
                spellCheck={false}
            />
            <button onClick={onSearch}>
                <svg
                    className="h-5 w-5 text-[#58708d]"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                >
                    <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth="2"
                        d="M21 21l-4.35-4.35m0 0A7.5 7.5 0 104.5 4.5a7.5 7.5 0 0012.15 12.15z"
                    />
                </svg>
            </button>
        </div>
    );
};

export default SearchBar;
