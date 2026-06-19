export interface DemoCategory {
    cno: number;
    cname: string;
}

export interface DemoSubCategory {
    scno: number;
    sname: string;
    cno: number;
}

export interface DemoProduct {
    pno: number;
    pname: string;
    price: number;
    pdesc: string;
    cno: number;
    scno: number;
    tno: number;
    delflag: boolean;
    attachFiles: { file_name: string }[];
}

export const demoCategories: DemoCategory[] = [
    { cno: 1, cname: "Travel Goods" },
    { cno: 2, cname: "Local Food" },
    { cno: 3, cname: "Lifestyle" },
];

export const demoSubCategories: DemoSubCategory[] = [
    { scno: 1, sname: "Bags", cno: 1 },
    { scno: 2, sname: "Snacks", cno: 2 },
    { scno: 3, sname: "Wellness", cno: 3 },
];

export const demoProducts: DemoProduct[] = [
    {
        pno: 1,
        pname: "Carry-on Organizer",
        price: 29000,
        pdesc: "Compact organizer for short trips.",
        cno: 1,
        scno: 1,
        tno: 1,
        delflag: false,
        attachFiles: [{ file_name: "m2.jpg" }],
    },
    {
        pno: 2,
        pname: "Local Snack Set",
        price: 18000,
        pdesc: "A curated snack pack for travelers.",
        cno: 2,
        scno: 2,
        tno: 1,
        delflag: false,
        attachFiles: [{ file_name: "m2.jpg" }],
    },
    {
        pno: 3,
        pname: "Travel Comfort Kit",
        price: 24000,
        pdesc: "Simple comfort items for long-distance travel.",
        cno: 3,
        scno: 3,
        tno: 2,
        delflag: false,
        attachFiles: [{ file_name: "m2.jpg" }],
    },
    {
        pno: 4,
        pname: "Light Day Pack",
        price: 39000,
        pdesc: "Lightweight bag for city tours.",
        cno: 1,
        scno: 1,
        tno: 1,
        delflag: false,
        attachFiles: [{ file_name: "m2.jpg" }],
    },
];
